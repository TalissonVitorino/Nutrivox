package com.kotlincrossplatform.nutrivox

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testHealthCheck() = testApplication {
        routing {
            get("/health") { call.respondText("OK") }
        }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }
}
