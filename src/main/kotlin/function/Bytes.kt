package me.parrot.function

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Reply
 * me.parrot.function.Bytes
 * 二进制数据操作
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 13:32
 */
fun ByteArray.zip(): ByteArray {
    return ByteArrayOutputStream().use { out ->
        GZIPOutputStream(out).use {
            it.write(this)
        }
        out.toByteArray()
    }
}

fun ByteArray.unzip(): ByteArray {
    return ByteArrayOutputStream().use { out ->
        GZIPInputStream(ByteArrayInputStream(this)).use {
            out.write(it.readBytes())
        }
        out.toByteArray()
    }
}