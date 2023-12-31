package me.parrot.mirai.data.content

import kotlinx.serialization.Serializable
import me.parrot.mirai.data.binary.ImageMetadata
import me.parrot.mirai.data.model.BinaryResource
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Reply
 * me.parrot.mirai.data.content.ImageContent
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:58
 */
@Serializable
data class ImageContent(val binaryId: Long) : Content {

    private val resource: BinaryResource
        get() = transaction {
            requireNotNull(BinaryResource.findById(binaryId)) { "Invalid binary id: $binaryId" }
        }

    context(MessageChainBuilder)
    override suspend fun append(origin: MessageEvent?) {
        origin ?: return
        val meta = resource.metadata as? ImageMetadata ?: return
        +meta.build(resource.value, origin.subject)
    }

}
