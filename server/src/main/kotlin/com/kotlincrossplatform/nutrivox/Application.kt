package com.kotlincrossplatform.nutrivox

import com.kotlincrossplatform.nutrivox.plugins.configureContentNegotiation
import com.kotlincrossplatform.nutrivox.plugins.configureStatusPages
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureContentNegotiation()
    configureStatusPages()

    routing {
        get("/") {
            call.respondText("Nutrivox API v1.0.0")
        }
        get("/health") {
            call.respondText("OK")
        }
    }
}
