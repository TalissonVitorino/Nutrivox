package com.kotlincrossplatform.nutrivox.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kotlincrossplatform.nutrivox.common.Exceptions.ConflictException
import com.kotlincrossplatform.nutrivox.common.Exceptions.ForbiddenException
import com.kotlincrossplatform.nutrivox.common.Exceptions.UnauthorizedException
import com.kotlincrossplatform.nutrivox.users.RefreshTokenTable
import com.kotlincrossplatform.nutrivox.users.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String,
    val phone: String? = null,
    val professionalRegistration: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

class AuthService(private val jwtConfig: JwtConfig) {

    fun register(request: RegisterRequest): UUID = transaction {
        val existingUser = UserTable
            .selectAll()
            .where { UserTable.email eq request.email }
            .firstOrNull()

        if (existingUser != null) {
            throw ConflictException("Email already registered")
        }

        val now = OffsetDateTime.now(ZoneOffset.UTC)

        UserTable.insert {
            it[email] = request.email
            it[passwordHash] = PasswordHasher.hash(request.password)
            it[role] = request.role
            it[fullName] = request.fullName
            it[phone] = request.phone
            it[professionalRegistration] = request.professionalRegistration
            it[isActive] = true
            it[createdAt] = now
            it[updatedAt] = now
        }[UserTable.id]
    }

    fun login(request: LoginRequest): TokenPair = transaction {
        val user = UserTable
            .selectAll()
            .where { UserTable.email eq request.email }
            .firstOrNull() ?: throw UnauthorizedException("Invalid credentials")

        if (!PasswordHasher.verify(request.password, user[UserTable.passwordHash])) {
            throw UnauthorizedException("Invalid credentials")
        }

        if (!user[UserTable.isActive]) {
            throw ForbiddenException("Account is deactivated")
        }

        val userId = user[UserTable.id]
        val role = user[UserTable.role]

        generateTokenPair(userId, role)
    }

    fun refresh(refreshToken: String): TokenPair = transaction {
        val tokenRow = RefreshTokenTable
            .selectAll()
            .where { RefreshTokenTable.token eq refreshToken }
            .firstOrNull() ?: throw UnauthorizedException("Invalid refresh token")

        val expiresAt = tokenRow[RefreshTokenTable.expiresAt]
        if (expiresAt.toInstant().isBefore(OffsetDateTime.now(ZoneOffset.UTC).toInstant())) {
            RefreshTokenTable.deleteWhere { RefreshTokenTable.token eq refreshToken }
            throw UnauthorizedException("Refresh token expired")
        }

        val userId = tokenRow[RefreshTokenTable.userId]

        // Delete used refresh token
        RefreshTokenTable.deleteWhere { RefreshTokenTable.token eq refreshToken }

        val user = UserTable
            .selectAll()
            .where { UserTable.id eq userId }
            .firstOrNull() ?: throw UnauthorizedException("User not found")

        if (!user[UserTable.isActive]) {
            throw ForbiddenException("Account is deactivated")
        }

        val role = user[UserTable.role]
        generateTokenPair(userId, role)
    }

    fun logout(userId: UUID): Unit = transaction {
        RefreshTokenTable.deleteWhere { RefreshTokenTable.userId eq userId }
    }

    private fun generateTokenPair(userId: UUID, role: String): TokenPair {
        val now = Date()

        val accessToken = JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withClaim("userId", userId.toString())
            .withClaim("role", role)
            .withIssuedAt(now)
            .withExpiresAt(Date(now.time + jwtConfig.accessTokenExpiration))
            .sign(Algorithm.HMAC256(jwtConfig.secret))

        val refreshTokenValue = UUID.randomUUID().toString()
        val refreshExpiration = OffsetDateTime.now(ZoneOffset.UTC)
            .plusSeconds(jwtConfig.refreshTokenExpiration / 1000)

        RefreshTokenTable.insert {
            it[RefreshTokenTable.userId] = userId
            it[token] = refreshTokenValue
            it[expiresAt] = refreshExpiration
            it[createdAt] = OffsetDateTime.now(ZoneOffset.UTC)
        }

        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshTokenValue
        )
    }
}
