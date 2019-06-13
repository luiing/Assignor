/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.cache

import com.uis.assignor.NO_TIME_OUT

interface ICache {

    fun readCache(name :String, mills: Long= NO_TIME_OUT, isDisk :Boolean= false):String

    fun writeCache(name :String, value: Any, isDisk :Boolean= false)

    fun removeCache(name :String, isDisk: Boolean= false)

    fun removeAllCache()
}
