/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.TypeConvert

internal class BodyStore(@Volatile var mState:Int = State_Created) :IState{
    private var models:ArrayMap<String,BodyModel> = ArrayMap()

    override fun onStateChanged(state: Int) {
        mState = state
        for(item in models.values){
            item.onStateChanged(state)
        }
        if(State_Destroy == state){
            models.clear()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T :BodyModel> get(f:(T)->Unit) :T {
        val type = TypeConvert.convert(f)
        val key = "BodyModel.default:".plus(type)
        return (models[key] ?: (type as Class<out BodyModel>).newInstance().apply {
            autoFindBodyModel()
            if (State_Resumed == mState) onStateChanged(mState)
            models[key] = this
        }) as T
    }
}