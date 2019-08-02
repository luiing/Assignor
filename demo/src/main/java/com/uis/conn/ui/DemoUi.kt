/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.uis.assignor.Assignor
import com.uis.conn.model.DemoModel
import com.uis.connector.demo.R
import kotlinx.android.synthetic.main.ui_demo.*

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
class DemoUi :Activity(){

    private val model = Assignor.of<DemoModel>(this){}

    override fun onCreate(savedInstanceState: Bundle?) {
        model.firstLoad()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_demo)
        bt_selt.setOnClickListener {
            startActivity(Intent(this,DemoUi::class.java))
        }
        bt_book.setOnClickListener {
            model.book()
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
    }

    private fun display(content: String){
        tv_content?.text = content
    }

}