package me.parrot.mirai.data.trigger

import kotlinx.serialization.Serializable
import me.parrot.mirai.data.Demand
import me.parrot.mirai.data.trigger.option.InstanceExclusiveOption
import me.parrot.mirai.data.trigger.option.InstanceSingletonOption
import me.parrot.mirai.registry.TriggerOptions
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageContent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.UnsupportedMessage

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

    companion object : ReplyTriggerParser<PlainText> {

        override val target: Class<PlainText>
            get() = PlainText::class.java

        override val name: String = "text"

        override val arguments: Map<String, List<String>> = mapOf(
            "text" to listOf("匹配内容"),
            "mode" to Mode.values().map { "${it.name} ${it.description}" },
            "ignoreCase" to listOf("忽略大小写")
        )

        override fun parse(demand: Demand): TextTrigger {
            val (text, mode, ignoreCase) = demand.positions
            return TextTrigger(text, Mode.valueOf(mode.uppercase()), ignoreCase.toBooleanStrict()).apply {
                TriggerOptions.parse(TextTrigger::class.java, demand)
                    .forEach { addOption(it) }
            }
        }

        override fun createDefault(message: PlainText, origin: MessageChain): TextTrigger {
            return TextTrigger(message.content).apply {
                val content = origin
                    .filterIsInstance<MessageContent>()
                    .filter { it !is UnsupportedMessage }
                val instance = content.filterIsInstance<PlainText>()
                if (instance.size == 1) {
                    addOption(InstanceSingletonOption)
                }
                if (instance.size == content.size) {
                    addOption(InstanceExclusiveOption)
                }
            }
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
