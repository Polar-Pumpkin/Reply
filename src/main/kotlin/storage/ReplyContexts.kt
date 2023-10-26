package me.parrot.storage

import kotlinx.serialization.json.Json
import me.parrot.Reply
import me.parrot.data.context.ReplyContext
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
 * me.parrot.storage.Contexts
 * 存储可复用的自动回复内容
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 09:38
 */
object ReplyContexts : LongIdTable("context") {
    val value = jsonb<ReplyContext>("value", Json)

    private val cached: MutableMap<EntityID<Long>, ReplyContext> = mutableMapOf()

    val size: Int
        get() = cached.size

    operator fun get(contextId: EntityID<Long>?): ReplyContext? = if (contextId != null) cached[contextId] else null

    fun match(context: ReplyContext): EntityID<Long>? =
        cached.entries.find { it.value.match(context) }?.key

    fun <T : ReplyContext> matchIsInstance(context: ReplyContext, clazz: Class<T>): EntityID<Long>? =
        cached.entries.find { it.value.match(context) && clazz.isInstance(it.value) }?.key

    inline fun <reified T : ReplyContext> matchIsInstance(context: ReplyContext): EntityID<Long>? =
        matchIsInstance(context, T::class.java)

    fun build() {
        cached.clear()
        transaction(Reply.db) {
            selectAll().forEach { cached[it[ReplyContexts.id]] = it[value] }
        }
        Reply.logger.info { "已加载 $size 项自动回复内容" }
    }

    fun upload(context: ReplyContext, force: Boolean = false): EntityID<Long> {
        val contextId = match(context)
        return if (contextId != null) {
            if (force) {
                transaction(Reply.db) {
                    update({ ReplyContexts.id eq contextId }) { it[value] = context }
                }
            }
            contextId
        } else {
            transaction(Reply.db) {
                insertAndGetId { it[value] = context }
                    .also { cached[it] = context }
            }
        }
    }
}