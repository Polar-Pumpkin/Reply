package me.parrot.mirai.data.model

import me.parrot.mirai.storage.Binaries
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Reply
 * me.parrot.mirai.data.model.BinaryResource
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 14:23
 */
class BinaryResource(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BinaryResource>(Binaries)

    var value by Binaries.value
    var metadata by Binaries.metadata
}