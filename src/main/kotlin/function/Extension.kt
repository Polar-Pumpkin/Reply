package me.parrot.mirai.function

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