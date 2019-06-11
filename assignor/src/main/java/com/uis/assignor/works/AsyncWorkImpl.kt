/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.works
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicInteger

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class AsyncWorkImpl :IAsyncWork{

    private val deque = LinkedBlockingDeque<()->AsyncResult>()

    private val atomic = AtomicInteger(-1)

    override fun async(work: () -> AsyncResult): IAsyncWork {
        deque.offer(work)
        return this
    }

    override fun asyncSize(size: Int): IAsyncWork {
        this.atomic.set(size)
        return this
    }

    override fun done(work: (MutableList<AsyncResult>) -> Unit) {
        Worker.ioExecute {
            /** 当设置了size,以设置size为准*/
            if(-1 == atomic.get()){
                atomic.set(deque.size)
            }
            val result = CopyOnWriteArrayList<AsyncResult>()
            while (atomic.get() > 0)
                deque.poll()?.apply {
                    Worker.ioExecute {
                        kotlin.runCatching {  result.add(this())}.exceptionOrNull()?.apply { printStackTrace() }
                        if(0 == atomic.decrementAndGet()){
                            work(result)
                            deque.clear()
                        }
                    }
                    return@apply
                }
        }
    }
}