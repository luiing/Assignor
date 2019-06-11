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

    override fun async(work: () -> AsyncResult): IAsyncWork {
        deque.offer(work)
        return this
    }

    override fun done(work: (MutableList<AsyncResult>) -> Unit) {
        val cnt = AtomicInteger(deque.size)
        val result = CopyOnWriteArrayList<AsyncResult>()
        while (deque.isNotEmpty())
            deque.poll()?.let {
                Worker.ioExecute {
                    kotlin.runCatching {  result.add(it())}.exceptionOrNull()?.apply { printStackTrace() }
                    if(0 == cnt.decrementAndGet()){
                        work(result)
                    }
                }
            }
    }
}