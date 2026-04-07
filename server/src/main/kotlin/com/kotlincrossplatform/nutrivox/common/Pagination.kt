package com.kotlincrossplatform.nutrivox.common

import io.ktor.server.application.*
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

data class PaginationParams(val page: Int, val pageSize: Int) {
    val offset get() = (page - 1) * pageSize
}

fun ApplicationCall.paginationParams(): PaginationParams {
    val page = request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val pageSize = request.queryParameters["pageSize"]?.toIntOrNull()?.coerceIn(1, 100) ?: 20
    return PaginationParams(page, pageSize)
}
