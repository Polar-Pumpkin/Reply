package me.parrot.mirai.internal.function

import me.parrot.mirai.data.Demand
import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.data.trigger.option.InstanceExclusiveOption
import me.parrot.mirai.data.trigger.option.InstanceSingletonOption
import me.parrot.mirai.data.trigger.option.LimitGroupOption
import me.parrot.mirai.registry.TriggerOptions
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.event.events.GroupAwareMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageContent
import net.mamoe.mirai.message.data.UnsupportedMessage

/**
 * Reply
 * me.parrot.mirai.internal.function.Extension
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:07
 */
internal inline fun <T : Message, reified E : ReplyTrigger<T>> E.parseOptions(demand: Demand): E {
    TriggerOptions.parse(E::class.java, demand)
        .forEach { addOption(it) }
    return this
}

context(MessageEvent)
internal fun <T : Message, E : ReplyTrigger<T>> E.createDefaultOptions(origin: MessageChain? = null): E {
    origin ?: return this
    val content = origin
        .filterIsInstance<MessageContent>()
        .filter { it !is UnsupportedMessage }
    val instance = content.mapNotNull(::cast)
    if (instance.size == 1) {
        addOption(InstanceSingletonOption)
    }
    if (instance.size == content.size) {
        addOption(InstanceExclusiveOption)
    }

    val event = this@MessageEvent
    if (event is GroupAwareMessageEvent && !event.toCommandSender().hasPermission(LimitGroupOption.bypass)) {
        addOption(LimitGroupOption(setOf(event.group.id)))
    }
    return this
}