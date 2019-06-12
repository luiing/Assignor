/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.cache

import android.support.v4.util.LruCache
import com.uis.assignor.CacheEntity
import com.uis.assignor.MAX_CACHE_SIZE
import com.uis.assignor.utils.MD5
import java.io.File
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

class CacheImpl(private var parent:File?, private var maxSize :Int= MAX_CACHE_SIZE):Cache {

    private val locks: LruCache<String, ReadWriteLock>
    private val lruCache: LruCache<String, CacheEntity>

    init {
        if(maxSize < 0){
            maxSize = MAX_CACHE_SIZE
        }

        parent?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        lruCache = CacheImpl.InnerCache(maxSize)
        locks = LruCache(20)
    }

    class InnerCache(maxSize: Int) : LruCache<String, CacheEntity>(maxSize) {
        override fun entryRemoved(evicted: Boolean, key: String, oldValue: CacheEntity, newValue: CacheEntity?) {
            if (evicted) {
                oldValue.data = null
            }
        }

        override fun sizeOf(key: String, value: CacheEntity): Int {
            return 1
        }
    }

    override fun readCache() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun writeCache() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getLock(key: String): ReadWriteLock {
        var lock = locks.get(key)
        if(lock == null){
            lock = ReentrantReadWriteLock()
            locks.put(key, lock)
        }
        return lock
    }

    private fun getOrCreatePath(key :String):File{
        val name = MD5.md5(key)
        val builder = StringBuilder()
        for (index in 0..2) {
            val dir = name.substring(2 * index, 2 * (index + 1))
            builder.append(dir).append(File.separatorChar)
        }
        val path = File(parent,builder.toString())
        if(!path.exists()){
            path.mkdirs()
        }
        return path
    }
}