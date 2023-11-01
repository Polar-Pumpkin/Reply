package me.parrot.mirai.command.action

import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent

/**
 * Reply
 * me.parrot.mirai.command.action.CommandAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 16:25
 */
interface CommandAction {

    suspend fun initialize(sender: UserCommandSender) {}

    suspend fun initialize(event: MessageEvent) {}

    suspend fun run(event: MessageEvent)

}