/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.cache

import android.support.v4.util.LruCache
import com.google.gson.Gson
import com.uis.assignor.CacheEntity
import com.uis.assignor.DEFAULT_CACHE_SIZE
import com.uis.assignor.NO_TIME_OUT
import com.uis.assignor.utils.ALog
import com.uis.assignor.utils.FileUtils
import com.uis.assignor.utils.MD5
import java.io.File

class CacheImpl(private var parent:File, private var maxSize :Int= DEFAULT_CACHE_SIZE):ICache {

    class BodyLruCache(maxSize: Int) : LruCache<String, CacheEntity>(maxSize) {
        override fun sizeOf(key: String, value: CacheEntity): Int {
            return value.size()
        }
    }

    private val dataCache: LruCache<String, CacheEntity> = BodyLruCache(maxSize)

    init {
        if(maxSize <= 0){
            maxSize = DEFAULT_CACHE_SIZE
        }
        if (!parent.exists()) {
            parent.mkdirs()
        }
    }

    override fun readCache(name: String, mills: Long, isDisk: Boolean): String {
        var entity:CacheEntity? = dataCache.get(name)
        if(entity == null && isDisk) {
            FileUtils.readFileInput(createFile(name))?.let {
                Gson().fromJson(String(it), CacheEntity::class.java)?.let {
                    entity = it
                    dataCache.put(name,it)
                }
            }
        }
        entity?.let {
            if (mills == NO_TIME_OUT || (System.currentTimeMillis() - it.mills) < mills) {
                return it.data
            }
        }
        return ""
    }

    override fun writeCache(name: String,value: Any, isDisk: Boolean) {
        val entity = CacheEntity(value)
        dataCache.put( name,entity)
        if(isDisk){
            val data = Gson().newBuilder().disableHtmlEscaping().create().toJson(entity)
            FileUtils.writeFileOutput(createFile(name),data.toByteArray())
        }
    }

    override fun removeCache(name: String, isDisk: Boolean) {
        dataCache.remove(name)
        if(isDisk){
            createFile(name).delete()
        }
    }

    override fun removeAllCache() {
        dataCache.evictAll()
        FileUtils.removeFileDirectory(parent)
    }

    internal fun createFile(key :String):File{
        val name = MD5.md5(key)
        val builder = StringBuilder()
        for (index in 0 until 5) {
            val dir = name.substring(2 * index, 2 * (index + 1))
            builder.append(dir).append(File.separatorChar)
        }
        val path = File(parent,builder.toString())
        if(!path.exists()){
            path.mkdirs()
        }
        return File(path,name)
    }
}