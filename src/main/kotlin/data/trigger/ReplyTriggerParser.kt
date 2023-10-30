package me.parrot.mirai.data.trigger

import me.parrot.mirai.data.Demand
import me.parrot.mirai.internal.flag.Unique
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain

/**
 * Reply
 * me.parrot.mirai.data.trigger.ReplyTriggerParser
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 10:25
 */
interface ReplyTriggerParser<T : Message> : Unique<String> {

    val arguments: Map<String, List<String>>

    context(MessageEvent)
    suspend fun parse(demand: Demand): ReplyTrigger<T>

    context(MessageEvent)
    suspend fun createDefault(message: T, origin: MessageChain): ReplyTrigger<T>

}