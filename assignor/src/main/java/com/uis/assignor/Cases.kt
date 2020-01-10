/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import com.google.gson.Gson
/**
 * @autho uis
 * @date 2019-05-30
 * @github https://github.com/luiing
 */
const val NO_TIME_OUT = -1L
const val DEFAULT_CACHE_SIZE = 10*1024*1024

const val State_Destroy = 1
const val State_SaveInstance = 2
const val State_Paused = 3
const val State_Created = 4
const val State_Resumed = 5

data class CacheEntity(private var arg:Any?, var mills:Long = System.currentTimeMillis()) {
    var data :String = ""

    init {
        arg?.apply {
            if(this is String){
                data = this
            }else{
                kotlin.runCatching {
                    data = Gson().newBuilder().disableHtmlEscaping().create().toJson(this)
                }.exceptionOrNull()?.printStackTrace()
            }
            arg = null
        }
    }

    fun size() :Int = data.length
}

data class AsyncResult(var name:String,var value:Any)

