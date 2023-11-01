package me.parrot.mirai.internal.function

import me.parrot.mirai.data.Demand
import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.data.trigger.option.InstanceExclusiveOption
import me.parrot.mirai.data.trigger.option.InstanceSingletonOption
import me.parrot.mirai.data.trigger.option.LimitGroupOption
import me.parrot.mirai.function.reply
import me.parrot.mirai.registry.TriggerOptions
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.event.events.GroupAwareMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import kotlin.math.ceil
import kotlin.math.roundToInt

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

context(CommandSender)
internal suspend fun <E> Collection<E>.onPage(page: Int, size: Long = 10L, action: MessageChainBuilder.(E) -> Unit) {
    val pages = ceil(this.size / size.toDouble()).roundToInt()
    if (page > pages) {
        return reply { +"未找到第 $page 页, 共 $pages 页" }
    }
    reply {
        +"第 $page 页 / 共 $pages 页\n"
        this@onPage.stream()
            .skip((page - 1) * size)
            .limit(size)
            .forEach { action(it) }
    }
}