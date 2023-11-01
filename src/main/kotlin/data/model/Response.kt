package me.parrot.mirai.data.model

import me.parrot.mirai.storage.Responses
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Reply
 * me.parrot.mirai.data.model.Response
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 15:10
 */
class Response(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Response>(Responses)

    var trigger by Responses.trigger
    var content by Responses.content
    var creator by Responses.creator
    val created by Responses.created
    var deleted by Responses.deleted
    var count by Responses.count

    context(MessageChainBuilder)
    suspend fun append(origin: MessageEvent? = null) {
        +"#${id.value}\n"
        +"触发器 (累计触发 $count 次):\n"
        +"$trigger\n"
        +"回复内容:\n"
        content.append(origin)
    }

}