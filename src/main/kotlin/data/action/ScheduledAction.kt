package me.parrot.data.action

import net.mamoe.mirai.event.events.MessageEvent

/**
 * Reply
 * me.parrot.data.action.ScheduledAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 21:55
 */
fun interface ScheduledAction {
    suspend fun execute(event: MessageEvent)
}