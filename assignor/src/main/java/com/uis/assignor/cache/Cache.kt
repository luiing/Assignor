/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.cache

import java.io.File

const val NO_TIME_OUT = -1
const val NO_PREFIX = ""
const val MAX_CACHE_SIZE = 200

interface Cache {
    fun getCache(key: String, mills: Long = -1): String
    fun putCache(key: String, entity: Any?, mills: Long = System.currentTimeMillis())
    fun writeFile(key: String, value: Any?): Boolean
    fun readFile(key: String, mills: Long, saveCache: Boolean = false): String

    fun clearCache(key: String,isDisk :Boolean= false)
    fun clearGroupCache(prefix: String,isDisk :Boolean= false)

    fun deleteFile(path: String)
    fun copyFile(res: File, des: File): Boolean
    fun saveFile(data: ByteArray, file: File,append: Boolean = false )
    fun getFileBytes(file: File): ByteArray
}
