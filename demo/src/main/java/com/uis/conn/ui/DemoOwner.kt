/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import com.uis.assignor.AssignorOwner
import com.uis.assignor.Body
import com.uis.assignor.BodyObserver
import com.uis.assignor.utils.ALog

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
class DemoOwner :AssignorOwner() {
    val stringBody = Body<String>()

    val intBody = Body<Int>()

    val listBody = Body<ArrayList<String>>()

    init {
        stringBody.addObserver(this,observer = object :BodyObserver<String>{
            override fun onBodyChanged(data: String) {
                ALog.e("data= $data")
            }
        })

        intBody.addObserver(this,observer = object :BodyObserver<Int>{
            override fun onBodyChanged(data: Int) {
                ALog.e("data= $data")
            }
        })

        listBody.addObserver(this,observer = object :BodyObserver<ArrayList<String>>{
            override fun onBodyChanged(data: ArrayList<String>) {
                ALog.e("data= ${data.toString()}")
            }
        })
    }
}