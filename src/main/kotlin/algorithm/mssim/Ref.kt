package me.parrot.algorithm.mssim

/**
 * Reply
 * me.parrot.algorithm.mssim.Ref
 * 成参考关系的两个相同对象
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 10:43
 */
data class Ref<T>(val origin: T, val other: T)