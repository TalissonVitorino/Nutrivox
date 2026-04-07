package com.kotlincrossplatform.nutrivox.audit

import com.kotlincrossplatform.nutrivox.users.UserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object AuditLogTable : Table("audit_log") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id).nullable()
    val entityType = varchar("entity_type", 100)
    val entityId = uuid("entity_id")
    val action = varchar("action", 50)
    val previousState = text("previous_state").nullable()
    val newState = text("new_state").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}
