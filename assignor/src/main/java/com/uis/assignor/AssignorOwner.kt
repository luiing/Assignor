/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
abstract class AssignorOwner {
    @Volatile private var mState = State_Created
        get() = mState

    fun onStateChange(state :Int){
        mState = state
    }
}