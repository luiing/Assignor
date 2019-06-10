/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import android.app.Activity
import android.os.Bundle
import com.uis.connector.demo.R
import kotlinx.android.synthetic.main.ui_main.*

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class DemoUi :Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_main)
        bt_action_a.setOnClickListener{
            syncCall()
        }
        bt_action_b.setOnClickListener {
            asyncCall()
        }
        bt_action_c.setOnClickListener {
            both()
        }
    }

    fun asyncCall(){

    }

    fun syncCall(){

    }

    fun both(){

    }
}