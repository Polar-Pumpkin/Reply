package me.parrot.mirai.manager

import me.parrot.mirai.data.History
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Reply
 * me.parrot.mirai.manager.Histories
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 13:11
 */
object Histories {

    private val users: MutableMap<Long, History> = mutableMapOf()

    operator fun get(userId: Long): History? = users[userId]

    operator fun set(userId: Long, history: History) {
        users[userId] = history
        transaction {
            history.responses.forEach {
                it.count++
                it.flush()
            }
        }
    }

}