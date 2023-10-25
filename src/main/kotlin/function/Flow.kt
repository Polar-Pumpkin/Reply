package me.parrot.function

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildMessageChain

/**
 * Reply
 * me.parrot.function.Flow
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 22:24
 */
suspend fun Contact.replyMessage(message: MessageChain, reply: suspend MessageChainBuilder.() -> Unit) {
    sendMessage(buildMessageChain {
        +message.quote()
        +" "
        +buildMessageChain { reply() }
    })
}

suspend fun Contact.replyMessage(message: MessageChain, vararg replies: String?) {
    replyMessage(message) {
        +replies.joinToString("\n") { "$it" }
    }
}

suspend inline fun MessageEvent.responsive(func: MessageEvent.() -> Unit) {
    try {
        func()
    } catch (ex: Throwable) {
        ex.printStackTrace()
        subject.replyMessage(message, "执行操作时遇到错误:", ex::class.java.canonicalName, ex.message)
    }
}