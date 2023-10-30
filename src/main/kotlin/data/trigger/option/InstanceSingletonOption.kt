package me.parrot.mirai.data.trigger.option

import kotlinx.serialization.Serializable

/**
 * Reply
 * me.parrot.mirai.data.trigger.option.InstanceAllowOtherOption
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 09:50
 */
@Serializable
object InstanceSingletonOption : TriggerOption, TriggerOptionParser {

    override val uniqueId: String = "singleton"

    override val description: List<String> = listOf("不允许「同类型」的消息存在")

    override fun parse(content: String): InstanceSingletonOption? = takeIf { content.toBooleanStrict() }

}