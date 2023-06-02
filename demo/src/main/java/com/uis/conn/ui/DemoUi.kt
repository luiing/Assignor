/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uis.anim.RotateCall
import com.uis.anim.RotateUtils
import com.uis.assignor.Assignor
import com.uis.assignor.utils.ALog
import com.uis.conn.data.Book
import com.uis.conn.kv.StorageManager
import com.uis.conn.model.DemoModel
import com.uis.connector.demo.databinding.UiDemoBinding
import kotlinx.coroutines.*

/**
 * @autho uis
 * @date 2019-06-06
 * @github https://github.com/luiing
 */
const val PI = 3.14
class DemoUi :AppCompatActivity(), RotateCall {

    private val model = Assignor.of<DemoModel>(this){}
    private lateinit var binding:UiDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UiDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btSelt.setOnClickListener {
            startActivity(Intent(this,DemoUi::class.java))
        }
        binding.btBook.setOnClickListener {
            model.book()
            firstRotate()
        }
        binding.btBookList.setOnClickListener {
            model.booklist()
            writeTest()
        }
        binding.btPerson.setOnClickListener {
            model.person()
        }
        binding.btSync.setOnClickListener {
            model.syncCall()
        }
        binding.btAsync.setOnClickListener {
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
        binding.tvContent.text = content
    }

    val storage = StorageManager(this)

    fun writeTest(){
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                storage.getDataStore()
                var t = System.currentTimeMillis()
                storage.dsWrite()
                costTime("dataStore",t)


                t = System.currentTimeMillis()
                storage.mmkvWrite()
                costTime("mmkv",t)


                t = System.currentTimeMillis()
                storage.spWrite()
                costTime("sp",t)
            }
        }
    }

    fun costTime(tag:String,t:Long){
        ALog.e("$tag cost ${System.currentTimeMillis()-t}")
    }
}