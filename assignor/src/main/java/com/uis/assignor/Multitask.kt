/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.case.MultitaskCase
import com.uis.assignor.callable.MultithtaskCall
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * 多线程并发任务回调
 * @author uis
 */

class Multitask(count: Int, private val callback: MultithtaskCall?) {

    private val multi: MultiAtomic = MultiAtomic(count)

    fun setResult(key: String, value: Any) {
        if (AssignorTask.isMainThread()) {
            AssignorTask.ioThread(Runnable { setResultCallback(key, value) })
        } else {
            setResultCallback(key, value)
        }
    }

    private fun setResultCallback(key: String, value: Any) {
        try {
            val cnt = multi.multiResponse(key, value)
            callback?.let {
                val index = multi.size() - cnt
                it.onProgress(index, key, value)
            }
            if (0 == cnt) {
                callback?.onMultitask(MultitaskCase(multi.resultMap))
                multi.reset()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }


    private class MultiAtomic(private val count: Int) {
        private val lock: Lock = ReentrantLock()

        private val cnt: AtomicInteger = AtomicInteger(count)
        val resultMap: MutableMap<String, Any> = ArrayMap()

        fun size(): Int {
            return count
        }

        fun multiResponse(key: String, value: Any): Int {
            try {
                lock.lock()
                resultMap[key] = value
                return cnt.decrementAndGet()
            }finally {
                lock.unlock()
            }
        }

        fun reset() {
            cnt.getAndSet(count)
            resultMap.clear()
        }
    }
}
