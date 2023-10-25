package me.parrot.algorithm.mssim

import java.awt.image.BufferedImage
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Reply
 * me.parrot.algorithm.mssim.Windows
 * 两张图像的参考窗口集合
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 10:50
 */
class Windows {

    constructor(origin: BufferedImage, other: BufferedImage) {
        require(origin.width == other.width) { "Image dimensions are not the same" }
        require(origin.height == other.height) { "Image dimensions are not the same" }
    }

    private fun scale(size: Int, scale: Int): Int {
        return (round(size / scale.toDouble()) * scale).roundToInt()
    }

}