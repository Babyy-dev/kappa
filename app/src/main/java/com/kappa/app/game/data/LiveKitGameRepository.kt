package com.kappa.app.game.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kappa.app.core.livekit.LiveKitRoomStore
import com.kappa.app.core.network.ApiService
import com.kappa.app.core.network.model.GameActionRequest
import com.kappa.app.core.network.model.GameGiftPlayRequest
import com.kappa.app.core.network.model.GameJoinRequest
import com.kappa.app.core.network.model.GameSessionRequest
import com.kappa.app.game.presentation.GameAction
import com.kappa.app.game.presentation.GamePlayer
import com.kappa.app.game.presentation.GameSessionEvent
import com.kappa.app.game.presentation.GameType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.launch
import timber.log.Timber

class LiveKitGameRepository(
    private val gson: Gson,
    private val apiService: ApiService,
    private val scope: CoroutineScope
) : GameRepository {

    private var currentSessionId: String = ""
    private var currentRoomId: String = ""
    private var currentUserId: String = ""
    private var eventSink: ((GameSessionEvent) -> Unit)? = null

    override fun connect(): Flow<GameSessionEvent> {
        return callbackFlow {
            eventSink = { event -> trySend(event) }
            val job = scope.launch(Dispatchers.IO) {
                LiveKitRoomStore.dataEvents.collect { message ->
                    parseEvent(message)?.let { event -> trySend(event) }
                }
            }
            awaitClose {
                eventSink = null
                job.cancel()
            }
        }
    }

    override fun joinGame(roomId: String, gameId: String, userId: String, entryFee: Long, type: GameType, balance: Long) {
        currentRoomId = roomId
        currentUserId = userId
        scope.launch(Dispatchers.IO) {
            val sessionResponse = runCatching { apiService.createGameSession(GameSessionRequest(roomId)) }.getOrNull()
            val sessionId = sessionResponse?.data?.sessionId
            if (sessionResponse == null || !sessionResponse.success || sessionId.isNullOrBlank()) {
                Timber.e("Game session create failed: ${sessionResponse?.error}")
                eventSink?.invoke(GameSessionEvent.Error(sessionResponse?.error ?: sessionResponse?.message ?: "Failed to create game session"))
                return@launch
            }
            currentSessionId = sessionId
            val joinResponse = runCatching {
                apiService.joinGame(
                    GameJoinRequest(
                        roomId = roomId,
                        userId = userId,
                        sessionId = sessionId,
                        gameId = gameId,
                        gameType = type.name.lowercase(),
                        entryFee = entryFee
                    )
                )
            }.getOrNull()
            if (joinResponse == null || !joinResponse.success) {
                Timber.e("Game join failed: ${joinResponse?.error}")
                eventSink?.invoke(GameSessionEvent.Error(joinResponse?.error ?: joinResponse?.message ?: "Failed to join game"))
                return@launch
            }
            val joinStatus = joinResponse.data?.status
            if (joinStatus != "ok") {
                eventSink?.invoke(GameSessionEvent.Error(joinResponse.data?.message ?: "Join rejected"))
                return@launch
            }
            eventSink?.invoke(GameSessionEvent.Joined(sessionId))
        }
    }

    override fun sendAction(action: GameAction) {
        if (currentSessionId.isBlank()) return
        scope.launch(Dispatchers.IO) {
            runCatching {
                apiService.sendGameAction(
                    GameActionRequest(
                        roomId = action.roomId,
                        userId = action.userId,
                        sessionId = currentSessionId,
                        action = action.action,
                        payload = action.payload?.mapValues { it.value?.toString() }
                    )
                )
            }.onSuccess { response ->
                if (!response.success) {
                    eventSink?.invoke(GameSessionEvent.Error(response.error ?: response.message ?: "Action failed"))
                    return@onSuccess
                }
                if (response.data?.status != "ok") {
                    eventSink?.invoke(GameSessionEvent.Error(response.data?.message ?: "Action rejected"))
                }
            }.onFailure {
                Timber.e(it, "Game action failed")
                eventSink?.invoke(GameSessionEvent.Error(it.message ?: "Action failed"))
            }
        }
    }

    override fun sendGiftPlay(roomId: String, userId: String, sessionId: String, giftId: String, quantity: Int) {
        val resolvedSession = if (sessionId.isBlank()) currentSessionId else sessionId
        if (resolvedSession.isBlank()) return
        scope.launch(Dispatchers.IO) {
            runCatching {
                apiService.sendGameGiftPlay(
                    GameGiftPlayRequest(
                        roomId = roomId,
                        userId = userId,
                        sessionId = resolvedSession,
                        giftId = giftId,
                        quantity = quantity
                    )
                )
            }.onSuccess { response ->
                if (!response.success) {
                    eventSink?.invoke(GameSessionEvent.Error(response.error ?: response.message ?: "Gift play failed"))
                    return@onSuccess
                }
                if (response.data?.status != "ok") {
                    eventSink?.invoke(GameSessionEvent.Error(response.data?.message ?: "Gift play rejected"))
                }
            }.onFailure {
                Timber.e(it, "Gift play failed")
                eventSink?.invoke(GameSessionEvent.Error(it.message ?: "Gift play failed"))
            }
        }
    }

    override fun leaveGame(gameId: String, sessionId: String) {
        // LiveKit-only: no socket to disconnect. Server state expires naturally.
    }

    override fun disconnect() {
        // Nothing to disconnect for LiveKit data channel.
    }

    private fun parseEvent(message: String): GameSessionEvent? {
        val json = runCatching { JsonParser.parseString(message).asJsonObject }.getOrNull() ?: return null
        val type = json.get("type")?.asString ?: return null
        val payload = json.getAsJsonObject("payload") ?: return null
        return when (type) {
            "state_update" -> parseState(payload)
            "reward" -> parseReward(payload)
            "error" -> GameSessionEvent.Error(payload.get("message")?.asString ?: "error")
            else -> null
        }
    }

    private fun parseState(payload: JsonObject): GameSessionEvent.State? {
        val roomId = payload.get("roomId")?.asString ?: return null
        val phase = payload.get("phase")?.asString ?: "lobby"
        val updatedAt = payload.get("updatedAt")?.asLong ?: 0L
        val timeLeft = payload.get("timeLeft")?.asInt ?: 0
        val pot = payload.get("pot")?.asLong ?: 0L
        val scoresObject = payload.getAsJsonObject("scores")
        val playersArray = payload.getAsJsonArray("players")
        val players = mutableListOf<GamePlayer>()
        playersArray?.forEach { item ->
            val id = item.asString
            if (id.isNotBlank()) {
                val score = scoresObject?.get(id)?.asInt ?: 0
                players.add(GamePlayer(id = id, name = id.take(6), score = score))
            }
        }
        return GameSessionEvent.State(
            roomId = roomId,
            phase = phase,
            players = players,
            updatedAt = updatedAt,
            timeLeft = timeLeft,
            pot = pot
        )
    }

    private fun parseReward(payload: JsonObject): GameSessionEvent.Result {
        val roomId = payload.get("roomId")?.asString ?: ""
        val status = payload.get("status")?.asString ?: "reward"
        val reward = payload.get("reward")?.asLong
        val balance = payload.get("balance")?.asLong
        return GameSessionEvent.Result(roomId = roomId, status = status, reward = reward, balance = balance)
    }
}
