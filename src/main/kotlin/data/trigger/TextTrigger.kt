package me.parrot.mirai.data.trigger

import kotlinx.serialization.Serializable
import me.parrot.mirai.data.Demand
import me.parrot.mirai.internal.function.createDefaultOptions
import me.parrot.mirai.internal.function.parseOptions
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText

/**
 * Reply
 * me.parrot.mirai.data.trigger.TextTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 16:23
 */
@Serializable
data class TextTrigger(
    val text: String,
    val mode: Mode = Mode.EQUAL,
    val ignoreCase: Boolean = false
) : ReplyTrigger<PlainText>() {

    override val clazz: Class<PlainText>
        get() = PlainText::class.java

    override suspend fun test(message: PlainText): Boolean {
        val content = message.content.trim()
        return when (mode) {
            Mode.EQUAL -> content.equals(text, ignoreCase)
            Mode.PREFIX -> content.startsWith(text, ignoreCase)
            Mode.SUFFIX -> content.endsWith(text, ignoreCase)
            Mode.EDGE -> content.startsWith(text, ignoreCase) || content.endsWith(text, ignoreCase)
            Mode.CONTAIN -> content.contains(text, ignoreCase)
        }
    }

    companion object : ReplyTriggerParser<PlainText, TextTrigger> {

        override val uniqueId: String = "text"

        override val clazz: Class<TextTrigger> = TextTrigger::class.java

        override val arguments: Map<String, List<String>> = mapOf(
            "text" to listOf("匹配内容"),
            "mode" to Mode.values().map { "${it.name} ${it.description}" },
            "ignoreCase" to listOf("是否忽略大小写")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): TextTrigger {
            val text = demand.argument("text", 0)
            val mode = demand.argument("mode", 1, Mode.EQUAL.name)
            val ignoreCase = demand.flag("ignoreCase", 2, false)
            return TextTrigger(text, Mode.valueOf(mode.uppercase()), ignoreCase)
                .parseOptions(demand)
        }

        context(MessageEvent)
        override suspend fun createDefault(message: PlainText, origin: MessageChain?): TextTrigger {
            return TextTrigger(message.content)
                .createDefaultOptions(origin)
        }

    }

    enum class Mode(val description: String) {
        EQUAL("与消息完全匹配"),
        PREFIX("位于消息头部"),
        SUFFIX("位于消息尾部"),
        EDGE("位于消息两端其一"),
        CONTAIN("被消息包含");
    }

}
