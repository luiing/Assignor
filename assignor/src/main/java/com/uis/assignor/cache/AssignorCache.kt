/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.cache

import android.support.v4.util.LruCache
import com.uis.assignor.AssignorTask
import com.uis.assignor.case.CacheEntity
import com.uis.assignor.utils.MD5
import java.io.File
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

class AssignorCache(private var parent:File?,private var maxSize :Int= MAX_CACHE_SIZE):Cache {

    private val locks: LruCache<String, ReadWriteLock>
    private val lruCache: LruCache<String, CacheEntity>

    init {
        if(maxSize < 0){
            maxSize = MAX_CACHE_SIZE
        }
        parent?:AssignorTask.app()?.let {
            parent = File(it.filesDir, "caches")
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

    override fun getCache(key: String, mills: Long): String {

    }

    override fun putCache(key: String, entity: Any?, mills: Long) {
    }

    override fun writeFile(key: String, value: Any?): Boolean {
    }

    override fun readFile(key: String, mills: Long, saveCache: Boolean): String {
    }

    override fun clearCache(key: String, isDisk: Boolean) {
    }

    override fun clearGroupCache(prefix: String, isDisk: Boolean) {
    }

    override fun deleteFile(path: String) {
    }

    override fun copyFile(res: File, des: File): Boolean {
    }

    override fun saveFile(data: ByteArray, file: File, append: Boolean) {

    }

    override fun getFileBytes(file: File): ByteArray {

    }
}