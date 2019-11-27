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
 * 自动生成[BodyDataContainer]使用的是无参构造函数
 */
open class BodyDataContainer :IState{
    private val states = LinkedList<IState>()
    private var calls :MutableList<(()->Unit)> = ArrayList()

    /** 自动注册 [BodyData] [IState] */
    internal fun autoFindBodyData(cls :Class<*>? =javaClass){
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
            autoFindBodyData(it.superclass)
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
            for (call in calls){
                call()
            }
            calls.clear()
        }
    }

    fun destroy(call :()->Unit){
        calls.add(call)
    }

    fun addState(state :IState){
        if(!states.contains(state)){
            states.offer(state)
        }
    }
}