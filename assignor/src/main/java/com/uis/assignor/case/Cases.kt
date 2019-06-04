/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.case

import com.google.gson.Gson

/**
 * @autho uis
 * @date 2019-05-30
 * @github https://github.com/luiing
 */

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

data class ErrorEntity(var code :Int=0,var error :String="",var other :String="")

data class MultitaskEntity (var result: Map<String, Any>)

data class AssignorResult<T>(var result :T?, var cacheKey: String = "", var isCache:Boolean = false)