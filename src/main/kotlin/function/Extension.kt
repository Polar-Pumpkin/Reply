package me.parrot.mirai.function

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildMessageChain
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Reply
 * me.parrot.mirai.function.Extension
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 13:13
 */
fun ByteArray.compress(): ByteArray {
    return ByteArrayOutputStream().use { out ->
        GZIPOutputStream(out).use {
            it.write(this)
        }
        out.toByteArray()
    }
}

fun ByteArray.decompress(): ByteArray {
    return ByteArrayOutputStream().use { out ->
        GZIPInputStream(ByteArrayInputStream(this)).use {
            out.write(it.readBytes())
        }
        out.toByteArray()
    }
}

suspend inline fun MessageEvent.reply(block: MessageChainBuilder.() -> Unit) {
    subject.sendMessage(buildMessageChain {
        +message.quote()
        +" "
        block()
    })
}

suspend inline fun CommandSender.reply(block: MessageChainBuilder.() -> Unit) {
    sendMessage(buildMessageChain {
        user?.let {
            +At(it)
            +" "
        }
        block()
    })
}