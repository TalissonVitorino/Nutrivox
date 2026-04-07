package com.kotlincrossplatform.nutrivox.common

object Exceptions {
    class NotFoundException(message: String) : RuntimeException(message)
    class UnauthorizedException(message: String = "Unauthorized") : RuntimeException(message)
    class ForbiddenException(message: String = "Forbidden") : RuntimeException(message)
    class ValidationException(message: String) : RuntimeException(message)
    class ConflictException(message: String) : RuntimeException(message)
}
