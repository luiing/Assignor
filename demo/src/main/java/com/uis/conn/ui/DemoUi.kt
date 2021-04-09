/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import com.uis.anim.RotateCall
import com.uis.anim.RotateUtils
import com.uis.assignor.Assignor
import com.uis.conn.model.DemoModel
import com.uis.connector.demo.R
import kotlinx.android.synthetic.main.ui_demo.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class DemoUi :Activity(), RotateCall {

    private val model = Assignor.of<DemoModel>(this){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_demo)
        bt_selt.setOnClickListener {
            startActivity(Intent(this,DemoUi::class.java))
        }
        bt_book.setOnClickListener {
            model.book()
            firstRotate()
        }
        bt_book_list.setOnClickListener {
            model.booklist()
        }
        bt_person.setOnClickListener {
            model.person()
        }
        bt_sync.setOnClickListener {
            model.syncCall()
        }
        bt_async.setOnClickListener {
            model.asyncCall()
        }

        model.string.observer {
            display(it)
        }
        model.book.observer {
            display(it.toString())
        }
        model.listBook.observer {
            display(it.toString())
        }
        model.person.observer {
            display(it.toString())
        }
        if (RotateUtils.isRotate(intent)) {
            lastRotate()
        }
        GlobalScope.launch{
            async {  }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("xx","onNewIntent....")
        if (RotateUtils.isRotate(intent)) {
            lastRotate()
        }
    }

    override fun rotateNextPage() {
        val intent = Intent(this,MainUi::class.java)
        intent.putExtra(RotateCall.ROTATE,true)
        startActivity(intent)
        overridePendingTransition(0, 0)
        lastRotate()
    }

    fun lastRotate(){
        val vv = findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        RotateUtils.applyLastRotation(vv, 90f, -0f)
    }

    fun firstRotate(){
        val vv = findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        RotateUtils.applyFirstRotation(this,vv,0f,-90f)
    }


    private fun display(content: String){
        tv_content?.text = content
    }

}