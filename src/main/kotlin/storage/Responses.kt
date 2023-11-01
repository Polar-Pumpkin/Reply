package me.parrot.mirai.storage

import kotlinx.serialization.json.Json
import me.parrot.mirai.data.content.Content
import me.parrot.mirai.data.trigger.ReplyTrigger
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Reply
 * me.parrot.mirai.storage.Responses
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 15:08
 */
object Responses : LongIdTable("response") {
    val trigger = jsonb<ReplyTrigger<*>>("condition", Json)
    val content = jsonb<Content>("content", Json)
    val creator = long("creator")
    val created = datetime("created").defaultExpression(CurrentDateTime)
    val deleted = datetime("deleted").nullable()
    val count = long("count").default(0)
}