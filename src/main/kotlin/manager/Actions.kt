package me.parrot.mirai.manager

import me.parrot.mirai.command.action.CommandAction
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent

/**
 * Reply
 * me.parrot.mirai.manager.Actions
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 16:25
 */
object Actions {

    private val scheduled: MutableMap<Long, CommandAction> = mutableMapOf()

    suspend fun schedule(sender: UserCommandSender, action: CommandAction) {
        action.initialize(sender)
        scheduled[sender.user.id] = action
    }

    suspend fun schedule(event: MessageEvent, action: CommandAction) {
        action.initialize(event)
        scheduled[event.sender.id] = action
    }

    fun cancel(userId: Long): Boolean = scheduled.remove(userId) != null

    operator fun get(userId: Long): CommandAction? = scheduled.remove(userId)

}