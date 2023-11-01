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
import kotlin.math.ceil
import kotlin.math.roundToInt

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

suspend fun MessageEvent.reply(block: suspend MessageChainBuilder.() -> Unit) {
    subject.sendMessage(buildMessageChain {
        +message.quote()
        +" "
        block()
    })
}

suspend fun CommandSender.reply(block: suspend MessageChainBuilder.() -> Unit) {
    sendMessage(buildMessageChain {
        user?.let {
            +At(it)
            +" "
        }
        block()
    })
}

fun Collection<*>.maxPage(size: Long = 10): Int {
    return ceil(this.size / size.toDouble()).roundToInt()
}

fun <E> Collection<E>.page(page: Int, size: Long = 10, action: (E) -> Unit) {
    stream()
        .skip((page - 1) * size)
        .limit(size)
        .forEach(action)
}