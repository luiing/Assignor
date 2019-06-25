/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.ALog

class BodyStore :IState{
    private var models:ArrayMap<String,BodyModel> = ArrayMap()

    override fun onStateChanged(state: Int) {
        for(item in models.values){
            item.onStateChanged(state)
        }
        if(State_Destroy == state){
            models.clear()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T :BodyModel> get(cls :Class<T>) :T {
        val key = "com.uis.assignor.BodyModel.default:".plus(cls.name).plus(cls.canonicalName)
        val owner = models[key] ?: {
            val it = Assignor.createModel(cls)
            it.autoFindBodyModel()
            models.put(key, it)
            it
        }()
        return  owner as T
    }
}