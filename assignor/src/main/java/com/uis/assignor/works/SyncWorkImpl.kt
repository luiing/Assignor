/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.works
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicInteger

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class SyncWorkImpl :ISyncWork{

    private val deque = LinkedBlockingDeque<(Any?)->Any>()
    private var atomic = AtomicInteger(-1)

    override fun sync(work: (Any?) -> Any): ISyncWork {
        deque.offer(work)
        return this
    }

    override fun syncSize(size: Int): ISyncWork {
        atomic.set(size)
        return this
    }

    override fun done(work: (Any?) -> Unit) {
        Worker.ioExecute {
            var value :Any? = null
            /** 当设置了size,已设置size为准*/
            if(-1 == atomic.get()){
                atomic.set(deque.size)
            }
            while (atomic.get() > 0) {
                deque.poll()?.let {
                    val old = value
                    value = it(old)
                    atomic.decrementAndGet()
                    return@let
                }
            }
            work(value)
            deque.clear()
        }
    }
}