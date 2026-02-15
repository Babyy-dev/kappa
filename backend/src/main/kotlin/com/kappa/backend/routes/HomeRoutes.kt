package com.kappa.backend.routes

import com.kappa.backend.data.HomeBanners
import com.kappa.backend.data.Posts
import com.kappa.backend.data.Users
import com.kappa.backend.models.ApiResponse
import com.kappa.backend.models.HomeBanner
import com.kappa.backend.models.HomePost
import com.kappa.backend.models.MiniGame
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.homeRoutes() {
    get("home/banners") {
        val banners = transaction {
            HomeBanners
                .select { HomeBanners.isActive eq true }
                .orderBy(HomeBanners.sortOrder to SortOrder.ASC, HomeBanners.updatedAt to SortOrder.DESC)
                .map { row ->
                    HomeBanner(
                        id = row[HomeBanners.id].toString(),
                        title = row[HomeBanners.title],
                        subtitle = row[HomeBanners.subtitle],
                        imageUrl = row[HomeBanners.imageUrl],
                        actionType = row[HomeBanners.actionType],
                        actionTarget = row[HomeBanners.actionTarget],
                        sortOrder = row[HomeBanners.sortOrder],
                        isActive = row[HomeBanners.isActive]
                    )
                }
        }
        call.respond(ApiResponse(success = true, data = banners))
    }

    get("home/mini-games") {
        val games = listOf(
            MiniGame("lucky_draw", "Lucky Draw", "Spin and win rewards", 200),
            MiniGame("battle_arena", "Battle Arena", "Score more in 30 seconds", 300),
            MiniGame("gift_rush", "Gift Rush", "Send gifts to climb the rank", 500)
        )
        call.respond(ApiResponse(success = true, data = games))
    }

    get("home/posts") {
        val posts = runCatching {
            transaction {
                (Posts innerJoin Users)
                    .selectAll()
                    .orderBy(Posts.createdAt to SortOrder.DESC)
                    .limit(50)
                    .map { row ->
                        val nickname = row[Users.nickname]
                        val username = row[Users.username]
                        HomePost(
                            id = row[Posts.id].toString(),
                            userId = row[Posts.userId].toString(),
                            userName = nickname ?: username,
                            content = row[Posts.content],
                            imageUrl = row[Posts.imageUrl],
                            avatarUrl = row[Users.avatarUrl],
                            createdAt = row[Posts.createdAt]
                        )
                    }
            }
        }.getOrElse { emptyList() }
        call.respond(ApiResponse(success = true, data = posts))
    }
}
