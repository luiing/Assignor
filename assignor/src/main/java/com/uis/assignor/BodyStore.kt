/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import androidx.collection.ArrayMap
import com.uis.assignor.utils.TypeConvert

class BodyStore(@Volatile var mState:Int = State_Created) :IState{
    private var models: ArrayMap<String, BodyModel> = ArrayMap()

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
    fun <B :BodyModel> get(f:(B)->Unit, name:String="") :B =  getModel(TypeConvert.convert(f) as Class<out BodyModel>,name)as B

    @Suppress("UNCHECKED_CAST")
    fun <B :BodyModel> get(cls:Class<B>, name:String="") :B = getModel(cls,name) as B

    fun remove(cls:Class<out BodyModel>, name:String="") = models.remove(getModelName(cls,name))

    private fun getModel(cls:Class<out BodyModel>, name:String=""):BodyModel {
        val key = getModelName(cls,name)
        return models[key] ?: cls.newInstance().apply {
            autoFindBodyData()
            if (State_Resumed == mState) onStateChanged(mState)
            models[key] = this
        }
    }

    private fun getModelName(cls:Class<*>,name:String=""):String = "BodyDataContainer.default${name}:${cls.name}"
}