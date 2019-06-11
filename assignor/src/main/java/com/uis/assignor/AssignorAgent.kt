/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.support.v4.util.ArrayMap

class AssignorAgent{
    private var bodyMap:ArrayMap<String,AssignorOwner> = ArrayMap()

    fun onStateChange(state :Int){
        for(item in bodyMap.values){
            item.onStateChange(state)
        }
        if(State_Destroy == state){
            bodyMap.clear()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T:AssignorOwner> get(cls :Class<T>) :T {
        val key = "com.uis.assignor.AssignorOwner.default:".plus(cls.name)
        return bodyMap[key]?.let { it as T } ?: {
            Assignor.createOwner(cls)
        }()
    }
}