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
 */
abstract class AssignorOwner :AssignorState{
    private val states = LinkedList<AssignorState>()
    private var destroyCall :(()->Unit)? = null

    override fun onStateChanged(state: Int) {
        for(item in states.iterator()){
            item.onStateChanged(state)
        }
        if(State_Destroy == state){
            removeStates()
            destroyCall?.apply {
                this()
            }
            destroyCall = null
        }
    }

    fun onDestroy(call :()->Unit){
        destroyCall = call
    }

    fun addState(state :AssignorState){
        if(!states.contains(state)){
            states.offer(state)
        }
    }

    fun removeStates(){
        states.clear()
    }
}