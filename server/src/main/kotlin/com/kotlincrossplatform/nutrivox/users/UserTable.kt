package com.kotlincrossplatform.nutrivox.users

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.statements.jdbc.JdbcPreparedStatementImpl

class PgEnumColumnType(private val enumTypeName: String) : ColumnType<String>() {
    override fun sqlType(): String = enumTypeName
    override fun valueFromDB(value: Any): String = value.toString()
    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val jdbcStmt = (stmt as JdbcPreparedStatementImpl).statement
        jdbcStmt.setObject(index, value, java.sql.Types.OTHER)
    }
}

fun Table.pgEnum(name: String, enumTypeName: String): Column<String> =
    registerColumn(name, PgEnumColumnType(enumTypeName))

object UserTable : Table("users") {
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = pgEnum("role", "user_role")
    val fullName = varchar("full_name", 255)
    val phone = varchar("phone", 50).nullable()
    val professionalRegistration = varchar("professional_registration", 100).nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object RefreshTokenTable : Table("refresh_tokens") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id)
    val token = varchar("token", 500).uniqueIndex()
    val expiresAt = timestampWithTimeZone("expires_at")
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}
