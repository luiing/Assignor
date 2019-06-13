/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import com.uis.assignor.utils.ALog
import java.util.*

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 * 自动生成[BodyModel]使用的是无参构造函数
 */
open class BodyModel :IState{
    private val states = LinkedList<IState>()
    private var destroyCall :(()->Unit)? = null

    /** 自动注册 [BodyData] [IState] */
    internal fun autoFindBodyModel(cls :Class<*>? =javaClass){
        cls?.let {
            for( field in it.declaredFields){
                //ALog.e("field name ${field.name},${field.type},"+(field.type == BodyData::class.java))
                if(isBodyData(field.type)){
                    field.isAccessible = true
                    field.get(this)?.let {
                        (it as? IState)?.let {state->
                            addState(state)
                        }
                    }
                }
            }
            autoFindBodyModel(it.superclass)
        }
    }

    internal fun isBodyData(cls :Class<*>) :Boolean{
        var type :Class<*>? = cls
        while(true){
            if(type == BodyData::class.java){
                return true
            }
            type?.superclass?.let {
                type = it
            } ?: return false
        }
    }

    override fun onStateChanged(state: Int) {
        for(item in states.iterator()){
            item.onStateChanged(state)
        }
        if(State_Destroy == state){
            states.clear()
            destroyCall?.apply {
                this()
                destroyCall = null
            }
        }
    }

    fun onDestroy(call :()->Unit){
        destroyCall = call
    }

    fun addState(state :IState){
        if(!states.contains(state)){
            states.offer(state)
        }
    }
}