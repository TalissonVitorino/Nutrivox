package com.kotlincrossplatform.nutrivox.plugins

import com.kotlincrossplatform.nutrivox.common.ApiResponse
import com.kotlincrossplatform.nutrivox.common.Exceptions.NotFoundException
import com.kotlincrossplatform.nutrivox.common.Exceptions.UnauthorizedException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ForbiddenException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ValidationException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ConflictException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiResponse.error(cause.message ?: "Not found"))
        }
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ApiResponse.error(cause.message ?: "Unauthorized"))
        }
        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, ApiResponse.error(cause.message ?: "Forbidden"))
        }
        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiResponse.error(cause.message ?: "Validation error"))
        }
        exception<ConflictException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, ApiResponse.error(cause.message ?: "Conflict"))
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiResponse.error(cause.message ?: "Invalid input"))
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Internal server error"))
        }
    }
}
