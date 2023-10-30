package me.parrot.mirai.data.model

import me.parrot.mirai.storage.Responses
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
class Response(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<Response>(Responses)

    var trigger by Responses.trigger
    var content by Responses.content
    var creator by Responses.creator
    val created by Responses.created
    var deleted by Responses.deleted
}