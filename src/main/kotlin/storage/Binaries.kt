package me.parrot.mirai.storage

import kotlinx.serialization.json.Json
import me.parrot.mirai.data.binary.BinaryMetadata
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb

/**
 * Reply
 * me.parrot.mirai.storage.Binaries
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 14:22
 */
object Binaries : LongIdTable("bin") {
    val value = blob("value")
    val metadata = jsonb<BinaryMetadata>("metadata", Json)
}