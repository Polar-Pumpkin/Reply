package me.parrot.storage

import kotlinx.serialization.json.Json
import me.parrot.Reply
import me.parrot.data.context.ReplyContext
import me.parrot.data.trigger.ReplyTrigger
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.utils.info
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * Reply
 * me.parrot.storage.ReplyDefines
 * 存储自动回复定义
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 09:17
 */
@Suppress("MemberVisibilityCanBePrivate")
object ReplyDefines : LongIdTable("define") {
    val trigger = jsonb<ReplyTrigger>("trigger", Json).uniqueIndex()
    val context = reference("context_id", ReplyContexts)

    private val indexes: MutableMap<EntityID<Long>, ReplyTrigger> = mutableMapOf()
    private val contexts: MutableMap<EntityID<Long>, EntityID<Long>> = mutableMapOf()

    val size: Int
        get() = indexes.size

    suspend fun match(message: MessageChain): Map.Entry<EntityID<Long>, ReplyTrigger>? =
        indexes.entries.find { (_, trigger) -> trigger.test(message) }

    fun getContextId(triggerId: EntityID<Long>): EntityID<Long>? = contexts[triggerId]

    fun build() {
        indexes.clear()
        contexts.clear()
        transaction(Reply.db) {
            selectAll().forEach {
                val defineId = it[ReplyDefines.id]
                val trigger = it[trigger]
                val contextId = it[context]
                indexes[defineId] = trigger
                contexts[defineId] = contextId
            }
        }
        Reply.logger.info { "已加载 $size 条自动回复" }
    }

    fun upload(trigger: ReplyTrigger, context: ReplyContext) {
        val contextId = ReplyContexts.upload(context)
        val triggerId = indexes.entries.find { it.value == trigger }?.key
        transaction(Reply.db) {
            if (triggerId != null) {
                update({ ReplyDefines.id eq triggerId }) {
                    it[ReplyDefines.context] = contextId
                }
            } else {
                val newId = insertAndGetId {
                    it[ReplyDefines.trigger] = trigger
                    it[ReplyDefines.context] = contextId
                }
                indexes[newId] = trigger
                contexts[newId] = contextId
            }
        }
    }

    suspend fun handle(event: MessageEvent) {
        val (triggerId, _) = indexes.entries.find { (_, trigger) -> trigger.test(event) } ?: return
        val contextId = getContextId(triggerId) ?: return
        val context = ReplyContexts[contextId] ?: return
        val response = context.build(event.subject).takeIf { it.isNotEmpty() } ?: return
        event.subject.sendMessage(response)
    }
}