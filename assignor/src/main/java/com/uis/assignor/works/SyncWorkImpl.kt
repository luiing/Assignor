/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.works
import java.util.concurrent.LinkedBlockingDeque

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class SyncWorkImpl :ISyncWork{

    private val deque = LinkedBlockingDeque<(Any?)->Any>()

    override fun sync(work: (Any?) -> Any): ISyncWork {
        deque.offer(work)
        return this
    }

    override fun done(work: (Any?) -> Unit) {
        Worker.ioExecute {
            var value :Any? = null
            while (deque.isNotEmpty()) {
                deque.poll()?.let {
                    val old = value
                    value = it(old)
                }
            }
            work(value)
        }
    }
}