package me.parrot.mirai.data.model

import me.parrot.mirai.storage.Links
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Reply
 * me.parrot.mirai.data.model.Link
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/31 16:26
 */
class Link(id: EntityID<Long>) : LongEntity(id) {

    companion object : LongEntityClass<Link>(Links) {

        private val regex = Regex("(?<active>\\d+)(?<direction><?->)(?<passive>\\d+)")

        fun solve(define: String): Triple<Long, Direction, Long>? {
            val result = regex.matchEntire(define) ?: return null
            val activeId = result.groups["active"]!!.value.toLong()
            val direction = Direction.values().first { it.connection == result.groups["direction"]!!.value }
            val passiveId = result.groups["passive"]!!.value.toLong()
            return Triple(activeId, direction, passiveId)
        }

    }

    var active by Response referencedOn Links.active
    var passive by Response referencedOn Links.passive
    var direction by Links.direction

    fun match(responses: Set<Long>): Boolean {
        if (passive.id.value in responses) {
            return true
        }
        if (direction == Direction.TWO_WAY) {
            return active.id.value in responses
        }
        return false
    }

    enum class Direction(val connection: String) {
        ONE_WAY("->"),
        TWO_WAY("<->");
    }

}