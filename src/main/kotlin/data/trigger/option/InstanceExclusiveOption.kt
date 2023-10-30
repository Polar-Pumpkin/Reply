package me.parrot.mirai.data.trigger.option

/**
 * Reply
 * me.parrot.mirai.data.trigger.option.InstanceExclusiveOption
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 10:04
 */
object InstanceExclusiveOption : TriggerOption, TriggerOptionParser {

    override val uniqueId: String = "exclusive"

    override val description: List<String> = listOf("不允许「不同类型」的消息存在")

    override fun parse(content: String): InstanceExclusiveOption? = takeIf { content.toBooleanStrict() }

}