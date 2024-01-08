package me.parrot.mirai.data.trigger

import kotlinx.serialization.Serializable
import me.parrot.mirai.data.Demand
import me.parrot.mirai.internal.function.parseOptions
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageSource

/**
 * Reply
 * me.parrot.mirai.data.trigger.SenderTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2024/01/08 16:05
 */
@Serializable
data class SenderTrigger(val userId: Long) : ReplyTrigger<MessageSource>() {

    override val isVirtual: Boolean
        get() = true

    override val clazz: Class<MessageSource>
        get() = TODO("Not yet implemented")

    override suspend fun test(message: MessageSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun test(event: MessageEvent): Boolean {
        return event.sender.id == userId
    }

    companion object : ReplyTriggerParser<MessageSource, SenderTrigger> {

        override val uniqueId: String = "from"

        override val clazz: Class<SenderTrigger> = SenderTrigger::class.java

        override val arguments: Map<String, List<String>> = mapOf(
            "userId" to listOf("消息发送用户 ID")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): SenderTrigger {
            val userId = demand.argument("userId", 0)
            return SenderTrigger(userId.toLong())
                .parseOptions(demand)
        }

        context(MessageEvent)
        override suspend fun createDefault(message: MessageSource, origin: MessageChain?): SenderTrigger {
            TODO("Not yet implemented")
        }

    }

}