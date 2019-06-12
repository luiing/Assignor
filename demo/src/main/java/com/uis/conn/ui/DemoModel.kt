/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import com.uis.assignor.BodyModel
import com.uis.assignor.BodyData
import com.uis.assignor.BodyObserver
import com.uis.assignor.utils.ALog

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
class DemoModel :BodyModel() {
    val stringBody = BodyData<String>()

    val intBody = BodyData<Int>()

    val listBody = BodyData<ArrayList<String>>()

    val demoBody = DemoData<Long>()

    val demoBody1 = DemoData<String>()
    val demoBody2 = DemoData<Long>()
    val demoBody3 = DemoData<List<String>>()
    val demoBody4 = DemoData<Long>()
    val demoBody5 = DemoData<Long>()
    val demoBody6 = DemoData<Long>()
    val demoBody7 = DemoData<Long>()

    fun action(){
        stringBody.setValue("dddd")
    }

}