package me.parrot.mirai.data

import me.parrot.mirai.data.model.Response

/**
 * Reply
 * me.parrot.mirai.data.History
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 14:01
 */
class History(val responses: List<Response>) {
    constructor(vararg response: Response) : this(listOf(*response))
}