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
import com.uis.assignor.Assignor
import com.uis.assignor.utils.ALog
import com.uis.assignor.works.AsyncResult
import com.uis.assignor.works.Worker
import com.uis.connector.demo.R
import kotlinx.android.synthetic.main.ui_main.*

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class DemoUi :Activity() {
    val model = Assignor.of(this).get(DemoModel::class.java)
    val test = Assignor.of(this).get(TestModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.ui_main)

        test.stringBody.setValue("onCreated....")
        test.stringBody.addObserver {data->
            ALog.e("string data= $data")
        }

        model.intBody.addObserver{data->
            ALog.e("int data= $data")
        }

        model.demoBody.addObserver { data->
            ALog.e("demo body data = $data")
        }

        model.intBody.addObserver{data->
            ALog.e("int data2= $data")
        }

        model.stringBody.addObserver{data->
            ALog.e("string data= $data")
        }
        model.listBody.addObserver{data->
            ALog.e("list data= ${data.toString()}")
        }

        bt_action_a.setOnClickListener{
            //syncCall()
            model.intBody.setValue(10)
            val it = Intent(this, DemoUi::class.java)
            startActivity(it)
        }
        bt_action_b.setOnClickListener {
            //asyncCall()
            Worker.ioExecute {
                test.stringBody.setValue("action clicked test1")
            }

            model.demoBody.setValue(1000)
        }
        bt_action_c.setOnClickListener {
            //both()
            val list = ArrayList<String>()
            list.add("001")
            list.add("002")
            model.listBody.setValue(list)

            ALog.e("cache value: "+test.stringBody.getValue()?.toString())
        }
        Worker.ioExecute {
            SystemClock.sleep(3000)
            test.stringBody.setValue("ioExecute")
        }
    }

    override fun onResume() {
        ALog.e("onResume")
        super.onResume()

    }

    override fun onDestroy() {
        test.stringBody.setValue("test onDestroy")
        super.onDestroy()
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