/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import android.app.Activity
import com.uis.assignor.Assignor
import com.uis.assignor.AssignorAgent

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
class DemoAgent() {
    lateinit var agent: AssignorAgent

    fun attach(activity :Activity){
        agent = Assignor.attach(activity)
    }

    fun string(string:String){
        agent.get(DemoOwner::class.java).stringBody.setValue(string)
    }

    fun int(){
        agent.get(DemoOwner::class.java).intBody.setValue(100)
    }

    fun list(){
        val list = ArrayList<String>()
        list.add("001")
        list.add("002")
        agent.get(DemoOwner::class.java).listBody.setValue(list)
    }
}