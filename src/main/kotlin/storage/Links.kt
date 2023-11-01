package me.parrot.mirai.storage

import me.parrot.mirai.data.model.Link
import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * Reply
 * me.parrot.mirai.storage.Links
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/31 16:20
 */
object Links : LongIdTable("link") {
    val active = reference("active", Responses)
    val passive = reference("passive", Responses)
    val direction = enumerationByName<Link.Direction>("direction", 255)
}