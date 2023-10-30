package me.parrot.mirai.registry

import me.parrot.mirai.data.trigger.ReplyTriggerParser
import me.parrot.mirai.internal.container.Registry
import net.mamoe.mirai.message.data.Message

/**
 * Reply
 * me.parrot.mirai.registry.Triggers
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 11:28
 */
object Triggers : Registry<Class<out Message>, ReplyTriggerParser<*>>()