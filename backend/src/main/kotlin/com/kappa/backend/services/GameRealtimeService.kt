package com.kappa.backend.services

import com.kappa.backend.models.GameActionRequest
import com.kappa.backend.models.GameActionResponse
import com.kappa.backend.models.GameJoinRequest
import com.kappa.backend.models.GameJoinResponse
import com.kappa.backend.models.GameStatePayload
import com.kappa.backend.models.GameEventEnvelope
import com.kappa.backend.models.GameGiftPlayRequest
import com.kappa.backend.models.GameGiftPayload
import com.kappa.backend.models.GameRewardPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.roundToLong

data class SessionInfo(
    val roomId: String,
    val userId: String,
    val sessionId: String,
    var gameId: String = "",
    var gameType: String = "lucky_draw",
    var lastActionAt: Long = 0L
)

data class GamePlayerState(
    val userId: String,
    var score: Int = 0,
    var entryPaid: Boolean = false
)

data class GameState(
    val roomId: String,
    var gameId: String = "",
    var gameType: String = "lucky_draw",
    var phase: String = "lobby",
    val players: MutableMap<String, GamePlayerState> = ConcurrentHashMap(),
    var updatedAt: Long = Instant.now().toEpochMilli(),
    var roundEndsAt: Long = 0L,
    var timeLeft: Int = 0,
    var pot: Long = 0L
)

class GameRealtimeService(
    private val sessionRegistry: GameSessionRegistry,
    private val economyService: EconomyService,
    private val liveKitRoomService: LiveKitRoomService
) {
    companion object {
        private const val ROUND_DURATION_SECONDS = 30
        private const val ROUND_DURATION_MILLIS = ROUND_DURATION_SECONDS * 1000L
    }

    private val logger = LoggerFactory.getLogger(GameRealtimeService::class.java)
    private val sessions = ConcurrentHashMap<String, SessionInfo>()
    private val roomStates = ConcurrentHashMap<String, GameState>()
    private val roundTimers = ConcurrentHashMap<String, Job>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json { encodeDefaults = false; ignoreUnknownKeys = true }

    fun join(request: GameJoinRequest): GameJoinResponse {
        val roomId = request.roomId.trim()
        val userId = request.userId.trim()
        val sessionId = request.sessionId.trim()
        if (roomId.isBlank() || userId.isBlank() || sessionId.isBlank()) {
            return GameJoinResponse(status = "error", message = "roomId, userId, sessionId required")
        }
        if (!sessionRegistry.validate(sessionId, roomId, userId)) {
            return GameJoinResponse(status = "error", message = "session invalid or expired")
        }
        val resolvedType = normalizeGameType(request.gameType, request.gameId)
        val resolvedGameId = request.gameId?.trim()?.ifBlank { null } ?: "game_$resolvedType"
        sessions[sessionId] = SessionInfo(
            roomId = roomId,
            userId = userId,
            sessionId = sessionId,
            gameId = resolvedGameId,
            gameType = resolvedType
        )
        val state = roomStates.computeIfAbsent(roomId) { GameState(roomId = roomId) }
        runCatching {
            synchronized(state) {
                if (state.phase != "started") {
                    state.gameType = resolvedType
                    state.gameId = resolvedGameId
                } else if (state.gameId.isBlank()) {
                    state.gameId = resolvedGameId
                }

                val player = state.players.getOrPut(userId) { GamePlayerState(userId) }
                val entryFee = request.entryFee?.coerceAtLeast(0L) ?: 0L
                if (entryFee > 0 && !player.entryPaid) {
                    economyService.debitCoins(UUID.fromString(userId), entryFee)
                    player.entryPaid = true
                    state.pot += entryFee
                }

                state.updatedAt = Instant.now().toEpochMilli()
                state.timeLeft = computeTimeLeft(state)
            }
        }.onFailure {
            return GameJoinResponse(status = "error", message = it.message ?: "join failed")
        }

        emitState(state, lastAction = null, payload = null, gift = null)
        return GameJoinResponse(status = "ok", sessionId = sessionId, state = state.toPayload(null, null, null))
    }

    fun action(request: GameActionRequest): GameActionResponse {
        val roomId = request.roomId.trim()
        val userId = request.userId.trim()
        val action = request.action.trim()
        if (roomId.isBlank() || userId.isBlank() || action.isBlank()) {
            return GameActionResponse(status = "error", message = "roomId, userId, action required")
        }
        val session = sessions[request.sessionId] ?: return GameActionResponse(status = "error", message = "session not joined")
        if (session.roomId != roomId || session.userId != userId) {
            return GameActionResponse(status = "error", message = "session mismatch")
        }
        val now = Instant.now().toEpochMilli()
        if (now - session.lastActionAt < 250) {
            return GameActionResponse(status = "error", message = "rate_limited")
        }
        session.lastActionAt = now
        val state = roomStates.computeIfAbsent(roomId) {
            GameState(roomId = roomId, gameId = session.gameId, gameType = session.gameType)
        }
        val normalizedAction = action.lowercase()
        var shouldStartRound = false
        var shouldFinishRound = false
        synchronized(state) {
            val player = state.players[userId] ?: return GameActionResponse(status = "error", message = "join game first")
            if (state.gameId.isBlank()) {
                state.gameId = session.gameId.ifBlank { request.payload?.get("gameId") ?: "game_${session.gameType}" }
            }
            if (state.gameType.isBlank()) {
                state.gameType = session.gameType
            }
            when (normalizedAction) {
                "start" -> {
                    shouldStartRound = state.phase != "started"
                    if (shouldStartRound) {
                        state.phase = "started"
                        state.roundEndsAt = now + ROUND_DURATION_MILLIS
                        state.timeLeft = ROUND_DURATION_SECONDS
                        state.players.values.forEach { it.score = 0 }
                    }
                }
                "end" -> {
                    shouldFinishRound = state.phase == "started"
                }
                else -> {
                    if (state.phase != "started") {
                        return GameActionResponse(status = "error", message = "game_not_started")
                    }
                    applyScore(state, player, normalizedAction)
                    state.timeLeft = computeTimeLeft(state)
                }
            }
            state.updatedAt = now
        }

        val payloadJson = request.payload?.let { payload ->
            buildJsonObject {
                payload.forEach { (key, value) ->
                    if (value != null) {
                        put(key, value)
                    }
                }
            }
        }

        if (shouldStartRound) {
            startRoundTimer(state.roomId)
        }
        if (shouldFinishRound) {
            finishRound(state.roomId, reason = "manual_end")
            return GameActionResponse(status = "ok")
        }

        emitState(state, lastAction = normalizedAction, payload = payloadJson, gift = null)
        return GameActionResponse(status = "ok")
    }

    fun giftPlay(request: GameGiftPlayRequest): GameActionResponse {
        val roomId = request.roomId.trim()
        val userId = request.userId.trim()
        val giftId = request.giftId.trim()
        val quantity = request.quantity
        if (roomId.isBlank() || userId.isBlank() || giftId.isBlank() || quantity <= 0) {
            return GameActionResponse(status = "error", message = "roomId, userId, giftId, quantity required")
        }
        val session = sessions[request.sessionId] ?: return GameActionResponse(status = "error", message = "session not joined")
        if (session.roomId != roomId || session.userId != userId) {
            return GameActionResponse(status = "error", message = "session mismatch")
        }
        val state = roomStates[roomId] ?: return GameActionResponse(status = "error", message = "join game first")
        val player = state.players[userId] ?: return GameActionResponse(status = "error", message = "join game first")
        if (state.phase != "started") {
            return GameActionResponse(status = "error", message = "game_not_started")
        }
        val giftUuid = runCatching { UUID.fromString(giftId) }.getOrNull()
            ?: return GameActionResponse(status = "error", message = "invalid gift id")
        val cost = economyService.getGiftCost(giftUuid) ?: return GameActionResponse(status = "error", message = "gift not found")
        val totalCost = cost * quantity.toLong()
        runCatching { economyService.debitCoins(UUID.fromString(userId), totalCost) }
            .onFailure { return GameActionResponse(status = "error", message = "insufficient_balance") }

        synchronized(state) {
            val scoreGain = calculateGiftScoreGain(state.gameType, totalCost, quantity)
            player.score += scoreGain
            state.pot += totalCost
            state.updatedAt = Instant.now().toEpochMilli()
            state.timeLeft = computeTimeLeft(state)
        }

        val payloadJson = buildJsonObject {
            put("scoreGain", calculateGiftScoreGain(state.gameType, totalCost, quantity))
            put("totalCost", totalCost)
        }
        emitState(
            state,
            lastAction = "gift_play",
            payload = payloadJson,
            gift = GameGiftPayload(giftId = giftId, quantity = quantity)
        )
        return GameActionResponse(status = "ok")
    }

    private fun applyScore(state: GameState, player: GamePlayerState, action: String) {
        val delta = when (state.gameType) {
            "lucky_draw" -> when (action) {
                "spin", "draw", "lucky" -> (10..100).random()
                else -> 0
            }
            "battle_arena" -> when (action) {
                "attack" -> (6..14).random()
                "defend" -> (2..6).random()
                "special" -> (12..22).random()
                else -> 1
            }
            "gift_rush" -> when (action) {
                "gift", "send_gift" -> (4..10).random()
                "combo" -> (10..16).random()
                else -> 2
            }
            "tap_speed" -> when (action) {
                "tap" -> 1
                "burst" -> 3
                else -> 1
            }
            else -> 1
        }
        player.score += max(0, delta)
    }

    private fun calculateGiftScoreGain(gameType: String, totalCost: Long, quantity: Int): Int {
        val base = max(1, (totalCost / 50L).toInt())
        return when (gameType) {
            "gift_rush" -> max(quantity, base * 2)
            else -> max(quantity, base)
        }
    }

    private fun startRoundTimer(roomId: String) {
        roundTimers.remove(roomId)?.cancel()
        val job = scope.launch {
            while (isActive) {
                delay(1000)
                val state = roomStates[roomId] ?: break
                var shouldFinish = false
                synchronized(state) {
                    if (state.phase != "started") {
                        return@launch
                    }
                    state.timeLeft = computeTimeLeft(state)
                    state.updatedAt = Instant.now().toEpochMilli()
                    shouldFinish = state.timeLeft <= 0
                }
                if (shouldFinish) {
                    finishRound(roomId, reason = "timer_end")
                    break
                }
                emitState(state, lastAction = null, payload = null, gift = null)
            }
        }
        roundTimers[roomId] = job
    }

    private fun finishRound(roomId: String, reason: String) {
        val state = roomStates[roomId] ?: return
        val rewards = mutableListOf<Pair<String, Long>>()
        var winnerId: String? = null
        var winnerScore = 0

        synchronized(state) {
            if (state.phase == "ended") {
                return
            }
            state.phase = "ended"
            state.roundEndsAt = 0L
            state.timeLeft = 0
            state.updatedAt = Instant.now().toEpochMilli()

            val ranking = state.players.values.sortedByDescending { it.score }
            winnerId = ranking.firstOrNull()?.userId
            winnerScore = ranking.firstOrNull()?.score ?: 0
            rewards.addAll(calculateRewards(state.pot, ranking.map { it.userId }))
            state.pot = 0L
            state.players.values.forEach { it.entryPaid = false }
        }

        roundTimers.remove(roomId)?.cancel()

        rewards.forEachIndexed { index, (userId, reward) ->
            if (reward <= 0L) {
                return@forEachIndexed
            }
            val balance = runCatching { economyService.creditCoins(UUID.fromString(userId), reward) }.getOrNull()
            val status = when (index) {
                0 -> "winner"
                1 -> "runner_up"
                else -> "top_player"
            }
            emitReward(roomId, userId, status, reward, balance?.balance)
        }

        val payloadJson = buildJsonObject {
            put("reason", reason)
            winnerId?.let { put("winnerId", it) }
            put("winnerScore", winnerScore)
        }
        emitState(state, lastAction = "round_end", payload = payloadJson, gift = null)
    }

    private fun calculateRewards(totalPot: Long, ranking: List<String>): List<Pair<String, Long>> {
        if (totalPot <= 0 || ranking.isEmpty()) {
            return emptyList()
        }
        val ratios = listOf(0.6, 0.3, 0.1)
        val winners = ranking.take(minOf(3, ranking.size))
        val rewards = mutableListOf<Pair<String, Long>>()
        var distributed = 0L
        winners.forEachIndexed { index, userId ->
            val share = if (index == winners.lastIndex) {
                totalPot - distributed
            } else {
                (totalPot * ratios[index]).roundToLong()
            }
            distributed += share
            rewards.add(userId to max(0L, share))
        }
        return rewards
    }

    private fun computeTimeLeft(state: GameState): Int {
        if (state.phase != "started" || state.roundEndsAt <= 0L) {
            return 0
        }
        val remainingMillis = state.roundEndsAt - Instant.now().toEpochMilli()
        if (remainingMillis <= 0L) {
            return 0
        }
        return ((remainingMillis + 999L) / 1000L).toInt().coerceAtLeast(0)
    }

    private fun normalizeGameType(rawType: String?, rawGameId: String?): String {
        val type = rawType?.trim()?.lowercase().orEmpty()
        val gameId = rawGameId?.trim()?.lowercase().orEmpty()
        val probe = if (type.isNotBlank()) type else gameId
        return when {
            probe.contains("lucky") || probe.contains("draw") -> "lucky_draw"
            probe.contains("battle") || probe.contains("arena") -> "battle_arena"
            probe.contains("gift") -> "gift_rush"
            probe.contains("tap") || probe.contains("speed") -> "tap_speed"
            else -> "lucky_draw"
        }
    }

    private fun emitReward(roomId: String, userId: String, status: String, reward: Long?, balance: Long?) {
        val payload = GameRewardPayload(roomId = roomId, userId = userId, status = status, reward = reward, balance = balance)
        val envelope = GameEventEnvelope(type = "reward", payload = payload)
        val jsonText = json.encodeToString(envelope)
        liveKitRoomService.sendData(roomId, jsonText.toByteArray(Charsets.UTF_8))
    }

    private fun emitState(
        state: GameState,
        lastAction: String?,
        payload: JsonObject?,
        gift: GameGiftPayload?
    ) {
        val payloadModel = state.toPayload(lastAction, payload, gift)
        val envelope = GameEventEnvelope(type = "state_update", payload = payloadModel)
        val jsonText = json.encodeToString(envelope)
        val ok = liveKitRoomService.sendData(state.roomId, jsonText.toByteArray(Charsets.UTF_8))
        if (!ok) {
            logger.warn("LiveKit state_update failed for room {}", state.roomId)
        }
    }

    private fun GameState.toPayload(
        lastAction: String?,
        payload: JsonObject?,
        gift: GameGiftPayload?
    ): GameStatePayload {
        return GameStatePayload(
            roomId = roomId,
            gameId = gameId,
            gameType = gameType,
            phase = phase,
            players = players.keys.toList(),
            scores = players.mapValues { (_, value) -> value.score },
            updatedAt = updatedAt,
            timeLeft = computeTimeLeft(this),
            pot = pot,
            lastAction = lastAction,
            payload = payload,
            gift = gift
        )
    }
}
