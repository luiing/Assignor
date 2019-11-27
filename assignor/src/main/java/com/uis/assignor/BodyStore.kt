/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.TypeConvert

class BodyStore(@Volatile var mState:Int = State_Created) :IState{
    private var models:ArrayMap<String,BodyDataContainer> = ArrayMap()

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
    fun <B :BodyDataContainer> get(f:(B)->Unit,name:String="") :B =  getModel(TypeConvert.convert(f) as Class<out BodyDataContainer>,name)as B

    @Suppress("UNCHECKED_CAST")
    fun <B :BodyDataContainer> get(cls:Class<B>,name:String="") :B = getModel(cls,name) as B

    fun remove(cls:Class<out BodyDataContainer>,name:String="") = models.remove(getModelName(cls,name))

    private fun getModel(cls:Class<out BodyDataContainer>,name:String=""):BodyDataContainer {
        val key = getModelName(cls,name)
        return models[key] ?: cls.newInstance().apply {
            autoFindBodyData()
            if (State_Resumed == mState) onStateChanged(mState)
            models[key] = this
        }
    }

    private fun getModelName(cls:Class<*>,name:String=""):String = "BodyDataContainer.default${name}:${cls.name}"
}