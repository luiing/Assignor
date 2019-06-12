/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import com.uis.assignor.utils.ALog
import com.uis.assignor.utils.TypeConvert
import com.uis.assignor.utils.TypeParam
import com.uis.assignor.works.AsyncResult
import com.uis.assignor.works.Worker
import com.uis.connector.demo.R
import kotlinx.android.synthetic.main.ui_main.*
import java.util.ArrayList

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class DemoUi :Activity() {

    val agent = DemoAgent()


    override fun onCreate(savedInstanceState: Bundle?) {
        agent.attach(this)
        agent.string("init created...")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_main)
        bt_action_a.setOnClickListener{
            //syncCall()
            agent.int()
            Worker.ioExecute {
                SystemClock.sleep(5000)
                agent.string("sleep 5000")
            }
            val it = Intent(this, DemoUi::class.java)
            startActivity(it)
        }
        bt_action_b.setOnClickListener {
            //asyncCall()
            agent.string("action clicked")
        }
        bt_action_c.setOnClickListener {
            //both()
            agent.list()
        }
    }

    fun asyncCall(){
        val async = Worker.asyncWork()
        async.async {
                    ALog.e("async 1")
                    return@async AsyncResult("001","1")
                }
                .async {
                    ALog.e("async 2")
                    return@async AsyncResult("002","2")
                }
                .async {
                    ALog.e("async 3")
                    return@async AsyncResult("003","3")
                }
                .async {
                    ALog.e("async 4")
                    return@async AsyncResult("004","4")
                }.done {
                    ALog.e("done:"+it.toString())
                }
    }

    fun syncCall(){
        val sync = Worker.syncWork()
        sync.sync {
                    ALog.e(it.toString())
                    "s1"
                }
                .sync {
                    ALog.e(it.toString())
                    "s2"
                }
                .sync {
                    ALog.e(it.toString())
                    "s3"
                }
                .sync {
                    ALog.e(it.toString())
                    "s4"
                }
                .done {
                    ALog.e("done:"+it.toString())
                }
    }

    fun both(){

    }
}