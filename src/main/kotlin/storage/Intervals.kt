package me.parrot.mirai.storage

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import me.parrot.mirai.Reply
import net.mamoe.mirai.event.events.GroupAwareMessageEvent
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.time.Duration

/**
 * Reply
 * me.parrot.mirai.storage.Intervals
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 14:42
 */
@Suppress("MemberVisibilityCanBePrivate")
object Intervals : LongIdTable("interval") {

    val userId = long("user_id")
    val subjectId = long("subject_id")
    val lastMessage = datetime("last_message")

    init {
        uniqueIndex(userId, subjectId)
    }

    fun days(event: GroupAwareMessageEvent, update: Boolean = true): Duration {
        return transaction(Reply.db) {
            val row = slice(lastMessage)
                .select { (userId eq event.sender.id) and (subjectId eq event.group.id) }
                .firstOrNull()
            if (update) {
                if (row != null) {
                    update({ (userId eq event.sender.id) and (subjectId eq event.group.id) }) {
                        it[lastMessage] = CurrentDateTime
                    }
                } else {
                    insert {
                        it[userId] = event.sender.id
                        it[subjectId] = event.group.id
                        it[lastMessage] = CurrentDateTime
                    }
                }
            }
            val timestamp = row?.get(lastMessage) ?: return@transaction Duration.ZERO
            Clock.System.now() - timestamp.toInstant(TimeZone.currentSystemDefault())
        }
    }

}