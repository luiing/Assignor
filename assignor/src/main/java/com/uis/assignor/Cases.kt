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
const val NO_TIME_OUT = -1
const val NO_PREFIX = ""
const val MAX_CACHE_SIZE = 200

const val State_Destroy = 1
const val State_SaveInstance = 2
const val State_Paused = 3
const val State_Created = 4
const val State_Resumed = 5

data class CacheEntity(val arg:Any?, var mills:Long = System.currentTimeMillis()) {
    var data:String? = null
    init {
        if(arg is String){
            data = arg
        }else{
            try{
                data = Gson().newBuilder().disableHtmlEscaping().create().toJson(arg)
            }catch (ex :Throwable){
                ex.printStackTrace()
            }
        }
    }
}

