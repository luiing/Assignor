/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import com.uis.assignor.AssignorOwner
import com.uis.assignor.AssignorBody
import com.uis.assignor.AssignorObserver
import com.uis.assignor.utils.ALog

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
class DemoOwner :AssignorOwner() {
    val stringBody = AssignorBody<String>()

    val intBody = AssignorBody<Int>()

    val listBody = AssignorBody<ArrayList<String>>()

    init {
        stringBody.addObserver(this,observer = object :AssignorObserver<String>{
            override fun onDataChanged(data: String) {
                ALog.e("data= $data")
            }
        })

        intBody.addObserver(this,observer = object :AssignorObserver<Int>{
            override fun onDataChanged(data: Int) {
                ALog.e("data= $data")
            }
        })

        listBody.addObserver(this,observer = object :AssignorObserver<ArrayList<String>>{
            override fun onDataChanged(data: ArrayList<String>) {
                ALog.e("data= ${data.toString()}")
            }
        })
    }
}