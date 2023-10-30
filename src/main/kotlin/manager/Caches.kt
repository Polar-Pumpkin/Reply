package me.parrot.mirai.manager

import me.parrot.mirai.Reply
import me.parrot.mirai.data.binary.BinaryMetadata
import me.parrot.mirai.data.model.BinaryResource
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Reply
 * me.parrot.mirai.manager.Caches
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 14:14
 */
object Caches {

    private val binaries: MutableMap<Long, BinaryResource> = mutableMapOf()

    fun build() {
        binaries.clear()
        transaction(Reply.db) {
            binaries += BinaryResource.all().associateBy { it.id.value }
        }
    }

    fun getBinaries(): Map<Long, BinaryResource> = binaries

    fun newBinary(blob: ExposedBlob, meta: BinaryMetadata): BinaryResource {
        return transaction(Reply.db) {
            BinaryResource.new {
                value = blob
                metadata = meta
            }
        }.also { binaries[it.id.value] = it }
    }

}