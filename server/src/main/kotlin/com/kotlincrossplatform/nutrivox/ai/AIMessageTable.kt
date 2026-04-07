package com.kotlincrossplatform.nutrivox.ai

import com.kotlincrossplatform.nutrivox.patients.PatientTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object AIConversationTable : Table("ai_conversations") {
    val id = uuid("id").autoGenerate()
    val patientId = uuid("patient_id").references(PatientTable.id)
    val contextType = varchar("context_type", 30)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object AIMessageTable : Table("ai_messages") {
    val id = uuid("id").autoGenerate()
    val conversationId = uuid("conversation_id").references(AIConversationTable.id)
    val role = varchar("role", 20)
    val content = text("content")
    val metadata = text("metadata").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    override val primaryKey = PrimaryKey(id)
}
