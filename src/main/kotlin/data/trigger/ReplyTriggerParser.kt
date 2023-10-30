package me.parrot.mirai.data.trigger

import me.parrot.mirai.data.Demand
import me.parrot.mirai.internal.flag.Unique
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
interface ReplyTriggerParser<T : Message> : Unique<Class<out Message>> {

    val target: Class<T>

    override val uniqueId: Class<out Message>
        get() = target

    val name: String

    val arguments: Map<String, List<String>>

    fun parse(demand: Demand): ReplyTrigger<T>

    fun createDefault(message: T, origin: MessageChain): ReplyTrigger<T>

}