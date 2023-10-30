package me.parrot.mirai.algorithm

import java.awt.image.BufferedImage

/**
 * Reply
 * me.parrot.mirai.algorithm.DHash
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 13:13
 */
@Suppress("SameParameterValue")
object DHash {

    fun compute(image: BufferedImage): CharArray {
        val width = 9
        val height = 8
        val resized = resize(image, width, height)

        val pixels = IntArray(width * height)
        for (x in 0 until width) {
            for (y in 0 until height) {
                pixels[x + y * width] = gray(resized.getRGB(x, y))
            }
        }

        return buildString {
            for (x in 0 until height) {
                for (y in 0 until width - 1) {
                    val index = x + y * width
                    if (pixels[index] >= pixels[index + 1]) {
                        append(1)
                    } else {
                        append(0)
                    }
                }
            }
        }.toCharArray()
    }

    fun diff(ref: CharArray, other: CharArray): Int {
        require(ref.size == other.size) { "Length not match: ${ref.size} <-> ${other.size}" }
        var distance = 0
        repeat(ref.size) { index ->
            if (ref[index] != other[index]) {
                distance++
            }
        }
        return distance
    }

    fun diff(ref: String, other: String): Int = diff(ref.toCharArray(), other.toCharArray())

    private fun gray(rgb: Int): Int {
        val a = (rgb shr 24) and 0xFF
        val r = (rgb shr 16) and 0xFF
        val g = (rgb shr 8) and 0xFF
        val b = rgb and 0xFF
        val v = (r * 77 + g * 151 + b * 28) shr 8
        return a or (v shl 16) or (v shl 8) or v
    }

    private fun resize(image: BufferedImage, width: Int, height: Int): BufferedImage {
        return BufferedImage(width, height, BufferedImage.TYPE_INT_BGR).apply {
            createGraphics().drawImage(image, 0, 0, width, height, null)
        }
    }

}