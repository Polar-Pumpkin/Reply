package me.parrot.algorithm.mssim

/**
 * Reply
 * me.parrot.algorithm.mssim.RefIterator
 * 两组成参考关系的对象集合的迭代器
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 10:45
 */
class RefIterator<E>(
    private val origin: Iterator<E>,
    private val other: Iterator<E>
) : Iterator<Ref<E>> {

    constructor(origins: Iterable<E>, others: Iterable<E>) : this(origins.iterator(), others.iterator())

    override fun hasNext(): Boolean {
        return origin.hasNext() && other.hasNext()
    }

    override fun next(): Ref<E> {
        val x = origin.next()
        val y = other.next()
        return Ref(x, y)
    }

}