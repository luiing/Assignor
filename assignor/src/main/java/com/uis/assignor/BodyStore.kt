/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.ALog
import com.uis.assignor.utils.TypeConvert

class BodyStore :IState{
    private var models:ArrayMap<String,BodyModel> = ArrayMap()
    @Volatile private var mState = State_Created

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
        var owner = models[key]
        if (null == owner) {
            type as Class<*>
            val model = type.newInstance()
            if(model is BodyModel) {
                model.autoFindBodyModel()
                if (State_Resumed == mState) {
                    model.onStateChanged(mState)
                }
                models[key] = model
                owner = model
            }
        }
        return owner as T
    }
}