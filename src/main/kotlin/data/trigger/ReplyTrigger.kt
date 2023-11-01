package me.parrot.mirai.data.trigger

import kotlinx.serialization.Serializable
import me.parrot.mirai.data.trigger.option.InstanceExclusiveOption
import me.parrot.mirai.data.trigger.option.InstanceSingletonOption
import me.parrot.mirai.data.trigger.option.LimitGroupOption
import me.parrot.mirai.data.trigger.option.TriggerOption
import me.parrot.mirai.function.optionId
import net.mamoe.mirai.event.events.GroupAwareMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageContent
import net.mamoe.mirai.message.data.UnsupportedMessage

/**
 * Reply
 * me.parrot.mirai.data.trigger.ReplyTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 14:35
 */
@Serializable
sealed class ReplyTrigger<T : Message> {

    private val options: MutableMap<String, TriggerOption> = mutableMapOf()

    abstract suspend fun test(message: T): Boolean

    open suspend fun test(message: MessageChain): Boolean {
        val content = message
            .filterIsInstance<MessageContent>()
            .filter { it !is UnsupportedMessage }
        val instance = content.mapNotNull(::cast)
        option<InstanceSingletonOption> {
            if (instance.size > 1) {
                return false
            }
        }
        option<InstanceExclusiveOption> {
            if (instance.size != content.size) {
                return false
            }
        }
        return instance.any { test(it) }
    }

    open suspend fun test(event: MessageEvent): Boolean {
        if (event is GroupAwareMessageEvent) {
            option<LimitGroupOption> {
                if (event.group.id !in it.groups) {
                    return false
                }
            }
        }
        return test(event.message)
    }

    @Suppress("UNCHECKED_CAST")
    open fun cast(message: Message): T? = message as? T

    fun addOption(option: TriggerOption, optionId: String = optionId(option::class.java)) {
        options[optionId] = option
    }

    fun removeOption(optionId: String) {
        options.remove(optionId)
    }

    fun getOptionOrNull(optionId: String): TriggerOption? = options[optionId]

    @JvmName("getOptionOrNullWithType")
    inline fun <reified T : TriggerOption> getOptionOrNull(optionId: String = optionId<T>()): T? =
        getOptionOrNull(optionId) as T?

    suspend inline fun <reified T : TriggerOption> option(optionId: String = optionId<T>(), block: (T) -> Unit) {
        getOptionOrNull<T>(optionId)?.let { block(it) }
    }

}