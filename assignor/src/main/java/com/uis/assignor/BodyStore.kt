/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.TypeConvert

class BodyStore(@Volatile var mState:Int = State_Created) :IState{
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
    fun <B :BodyModel> get(f:(B)->Unit) :B =  getModel(TypeConvert.convert(f) as Class<out BodyModel>)as B

    @Suppress("UNCHECKED_CAST")
    fun <B :BodyModel> get(cls:Class<B>) :B = getModel(cls) as B

    fun remove(cls:Class<out BodyModel>) = models.remove(getModelName(cls))

    private fun getModel(cls:Class<out BodyModel>):BodyModel {
        val key = getModelName(cls)
        return models[key] ?: cls.newInstance().apply {
            autoFindBodyModel()
            if (State_Resumed == mState) onStateChanged(mState)
            models[key] = this
        }
    }

    private fun getModelName(cls:Class<*>):String = "BodyModel.default:${cls.name}"
}