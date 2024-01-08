package me.parrot.mirai.data.model

import me.parrot.mirai.function.reply
import me.parrot.mirai.storage.Responses
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Reply
 * me.parrot.mirai.data.model.Response
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 15:10
 */
class Response(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Response>(Responses) {

        suspend fun isExclusive(event: MessageEvent): Boolean {
            return newSuspendedTransaction {
                val responses = find { Responses.deleted.isNull() }
                    .filter { !it.trigger.isVirtual && it.trigger.test(event) }
                    .toList()
                if (responses.isNotEmpty()) {
                    event.reply {
                        +"此触发器已有 ${responses.size} 项自动回复:\n"
                        responses.forEach {
                            +"#${it.id.value} ${it.trigger}"
                        }
                    }
                    false
                } else {
                    true
                }
            }
        }

    }

    var trigger by Responses.trigger
    var content by Responses.content
    var creator by Responses.creator
    val created by Responses.created
    var deleted by Responses.deleted
    var count by Responses.count

    context(MessageChainBuilder)
    suspend fun append(origin: MessageEvent? = null) {
        +"#${id.value}\n"
        +"【触发器 (累计触发 $count 次)】\n"
        +"$trigger\n"
        +"【回复内容】\n"
        content.append(origin)
    }

}