/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.cache

import android.support.v4.util.LruCache
import android.text.TextUtils

import com.google.gson.Gson
import com.uis.assignor.case.CacheCase
import com.uis.assignor.AssignorTask
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * @author uis
 */
class CacheImpl(private var maxSize: Int, directory: File?=null) : Cache {
    private var parent: File? = null
    private val locks: LruCache<String, ReadWriteLock>
    private val lruCache: LruCache<String, CacheCase>

    @JvmOverloads
    constructor(directory: File? = null) : this(200, directory) {
    }

    init {
        if (maxSize <= Cache.NO_TIME_OUT) {
            maxSize = 200
        }
        try {
            if (directory != null && directory.isDirectory) {
                directory.mkdirs()
            }
            parent = directory
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        lruCache = InnerCache(maxSize)
        locks = LruCache(20)
    }


    class InnerCache(maxSize: Int) : LruCache<String, CacheCase>(maxSize) {

        override fun entryRemoved(evicted: Boolean, key: String, oldValue: CacheCase, newValue: CacheCase?) {
            if (evicted) {
                oldValue.data = null
            }
        }

        override fun sizeOf(key: String, value: CacheCase): Int {
            return 1
        }
    }

    private fun getFilePath(key: String): File? {
        var file: File? = null
        if (null == parent && AssignorTask.app() != null) {
            parent = File(AssignorTask.app()!!.filesDir, "caches")
            if (!parent!!.exists()) {
                parent!!.mkdirs()
            }
        }
        val alis = AssignorTask.md5(key)
        if (parent != null && !TextUtils.isEmpty(alis)) {
            val builder = StringBuilder()
            for (index in 0..2) {
                val dir = alis.substring(2 * index, 2 * (index + 1))
                builder.append(dir).append(File.separatorChar)
            }
            val absPath: File
            val path = builder.toString()
            if (!TextUtils.isEmpty(path)) {
                absPath = File(parent, path)
                if (!absPath.exists()) {
                    absPath.mkdirs()
                }
            } else {
                absPath = parent
            }
            file = File(absPath, key)
        }
        return file
    }

    override fun clearMemory(key: String) {
        lruCache.remove(key)
    }

    override fun clearAllMemory(prefix: String) {
        if (TextUtils.isEmpty(prefix)) {
            lruCache.evictAll()
        } else {
            val keys = lruCache.snapshot().keys
            for (key in keys) {
                if (key.startsWith(prefix)) {
                    lruCache.remove(key)
                }
            }
            keys.clear()
        }
    }

    override fun clear(key: String) {
        clearMemory(key)
        deleteFile(key)
    }

    override fun clearAll(prefix: String) {
        clearAllMemory(prefix)
        if (parent != null) {
            deleteFiles(parent, prefix)
        }
    }

    private fun deleteFiles(root: File, prefix: String) {//递归删除
        try {
            var file: File
            for (name in root.list()) {
                file = File(root, name)
                if (file.isDirectory) {
                    deleteFiles(file, prefix)
                    file.delete()
                } else if (TextUtils.isEmpty(prefix) || name.startsWith(prefix)) {
                    file.delete()
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

    }

    override fun get(key: String): String? {
        return get(key, Cache.NO_TIME_OUT.toLong())
    }

    override fun get(key: String, mills: Long): String? {
        var result: String? = null
        try {
            val entity = if (TextUtils.isEmpty(key)) null else lruCache.get(key)
            if (entity != null) {
                if (mills <= 0 || System.currentTimeMillis() - entity!!.mills < mills) {
                    result = entity!!.result
                } else {
                    lruCache.remove(key)
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        return result
    }

    override fun put(key: String, entity: Any) {
        put(key, entity, System.currentTimeMillis())
    }

    override fun put(key: String, value: Any?, mills: Long) {
        if (!TextUtils.isEmpty(key)) {
            if (null == value) {
                clearMemory(key)
            } else {
                val entity = CacheCase(value, mills)
                if (!TextUtils.isEmpty(entity.data)) {
                    lruCache.put(key, entity)
                } else {
                    clearMemory(key)
                }
            }
        }
    }

    private fun getLock(key: String): ReadWriteLock {
        var lock = locks.get(key)
        if (lock == null) {
            lock = ReentrantReadWriteLock()
            locks.put(key, lock)
        }
        return lock
    }

    override fun writeFile(key: String, value: Any?): Boolean {
        var isSuccess = false
        if (!TextUtils.isEmpty(key)) {
            val lock = getLock(key)
            lock.writeLock().lock()
            try {
                val file = getFilePath(key)
                if (value == null) {
                    file!!.delete()
                } else {
                    val json = Gson().newBuilder().disableHtmlEscaping().create().toJson(CacheCase(value, System.currentTimeMillis()))
                    val fileOut = FileOutputStream(file)
                    fileOut.write(json.toByteArray())
                    fileOut.flush()
                    fileOut.close()
                    isSuccess = true
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

            lock.writeLock().unlock()
        }
        return isSuccess
    }

    override fun readFile(key: String): String? {
        return readFile(key, Cache.NO_TIME_OUT.toLong())
    }

    override fun readFile(key: String, mills: Long): String? {
        return readFile(key, mills, false)
    }

    override fun readFile(key: String, mills: Long, saveCache: Boolean): String? {
        var value: String? = null
        if (!TextUtils.isEmpty(key)) {
            val lock = getLock(key)
            lock.readLock().lock()
            try {
                val file = getFilePath(key)
                val fileIn = FileInputStream(file)
                val bytes = ByteArray(file!!.length().toInt())
                fileIn.read(bytes)
                fileIn.close()
                val result = String(bytes, Charset.forName("UTF-8"))
                val entity = Gson().fromJson(result, CacheCase::class.java)
                if (entity != null) {
                    if (mills <= 0 || System.currentTimeMillis() - entity!!.mills < mills) {
                        value = entity.data
                    }
                }
                if (saveCache) {
                    val currentMills = if (value == null) System.currentTimeMillis() else entity!!.mills
                    put(key, value, currentMills)
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

            lock.readLock().unlock()
        }
        return value
    }

    override fun deleteFile(key: String) {
        if (!TextUtils.isEmpty(key)) {
            try {
                val file = getFilePath(key)
                file!!.delete()
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }

        }
    }

    override fun copyFile(res: File, des: File): Boolean {
        var success = true
        var channelRes: FileChannel? = null
        var channelDes: FileChannel? = null
        try {
            channelRes = RandomAccessFile(res, "rwd").channel
            channelDes = RandomAccessFile(des, "rwd").channel
            channelRes!!.transferTo(0, res.length(), channelDes)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            success = false
        }

        try {
            channelRes?.close()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        try {
            channelDes?.close()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        return success
    }

    override fun saveFile(isAppend: Boolean, data: ByteArray, file: File) {
        var channel: FileChannel? = null
        try {
            if (!file.exists()) {
                val path = file.parentFile
                if (!path.exists()) {
                    path.mkdirs()
                }
            }
            if (!isAppend && file.exists()) {
                file.delete()
            }
            channel = RandomAccessFile(file, "rwd").channel
            channel!!.lock()
            if (isAppend) {
                channel.position(file.length())
            }
            val size = data.size
            val byteBuffer = ByteBuffer.allocate(size)
            byteBuffer.put(data, 0, size)
            byteBuffer.flip()
            channel.write(byteBuffer)
            byteBuffer.clear()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        try {
            channel?.close()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }

    override fun getFile(file: File): ByteArray {
        var channel: FileChannel? = null
        var data = ByteArray(0)
        try {
            if (file.exists()) {
                channel = RandomAccessFile(file, "rwd").channel
                channel?.lock()
                data = ByteArray(file.length().toInt())
                val byteBuffer = ByteBuffer.allocate(1024)
                var total = 0
                var len = channel.read(byteBuffer)
                while (len != -1) {
                    byteBuffer.flip()
                    byteBuffer.get(data, total, len)
                    total += len
                    byteBuffer.clear()
                    len = channel.read(byteBuffer)
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        try {
            channel?.close()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }

        return data
    }
}
