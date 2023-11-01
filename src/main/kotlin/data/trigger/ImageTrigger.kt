package me.parrot.mirai.data.trigger

import me.parrot.mirai.data.Demand
import me.parrot.mirai.data.binary.ImageMetadata
import me.parrot.mirai.data.model.BinaryResource
import me.parrot.mirai.internal.function.createDefaultOptions
import me.parrot.mirai.internal.function.parseOptions
import me.parrot.mirai.manager.Caches
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.getValue

/**
 * Reply
 * me.parrot.mirai.data.trigger.ImageTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 13:09
 */
data class ImageTrigger(val binaryId: Long) : ReplyTrigger<Image>() {

    private val resource: BinaryResource
        get() = Caches.getBinary(binaryId)

    override suspend fun test(message: Image): Boolean {
        return (resource.metadata as? ImageMetadata)?.isSimilar(message) ?: return false
    }

    companion object : ReplyTriggerParser<Image, ImageTrigger> {

        override val uniqueId: String = "image"

        override val clazz: Class<ImageTrigger> = ImageTrigger::class.java

        override val arguments: Map<String, List<String>> = mapOf(
            "image" to listOf("图片")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): ImageTrigger {
            val (code) = demand.positions
            val chain = code.deserializeMiraiCode(subject)
            val image: Image by chain
            return of(image)
                .parseOptions(demand)
        }

        context(MessageEvent)
        override suspend fun createDefault(message: Image, origin: MessageChain): ImageTrigger {
            return of(message)
                .createDefaultOptions(message, origin)
        }

        private suspend fun of(image: Image): ImageTrigger {
            return ImageTrigger((ImageMetadata.find(image) ?: ImageMetadata.upload(image)).id.value)
        }

    }

}
