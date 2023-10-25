package me.parrot.algorithm.mssim

import java.awt.image.BufferedImage

/**
 * Reply
 * me.parrot.algorithm.mssim.Window
 * MSSIM 算法的滑动窗口
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 10:29
 */
class Window(
    image: BufferedImage,
    private val size: Int,
    private val x: Int,
    private val y: Int
) {

    val luma = calculate(image)
    val avg = luma.average()

    private fun calculate(image: BufferedImage): DoubleArray {
        val values = DoubleArray(size * size)
        var index = 0
        for (i in x until x + size) {
            for (j in y until y + size) {
                val rgb = image.getRGB(i, j)
                val r = (rgb shr 16) and 0xFF
                val g = (rgb shr 8) and 0xFF
                val b = rgb and 0xFF
                values[index++] = listOf(
                    r.toDouble() * RED_COEFFICIENT,
                    g.toDouble() * GREEN_COEFFICIENT,
                    b.toDouble() * BLUE_COEFFICIENT
                ).sum()
            }
        }
        return values
    }

    companion object {
        // REC 601 coefficients for standard def digital formats
        // http://en.wikipedia.org/wiki/Luma_(video)
        private const val RED_COEFFICIENT = 0.212655F
        private const val GREEN_COEFFICIENT = 0.715158F
        private const val BLUE_COEFFICIENT = 0.072187F
    }

}
