/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.model

import android.os.SystemClock
import com.uis.assignor.AsyncResult
import com.uis.assignor.BodyModel
import com.uis.assignor.BodyData
import com.uis.assignor.utils.ALog
import com.uis.assignor.works.Worker
import com.uis.conn.data.Book
import com.uis.conn.data.PersonInfo

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
class DemoModel :BodyModel() {
    val person = BodyData<PersonInfo>()
    val book = BodyData<Book>()
    val listBook = BodyData<ArrayList<Book>>()
    val string = BodyData<String>()

    fun firstLoad(){
        string.setValue("first load ${System.currentTimeMillis()}")
    }

    fun book(){
        Worker.ioExecute {
            SystemClock.sleep(1000)
            book.setValue(Book("Kotlin","35.00","https://www.kotlin.org"))
        }
    }

    fun booklist(){
        val list = ArrayList<Book>()
        list.add(Book("Kotlin","35.00","https://www.kotlin.org"))
        list.add(Book("Java","25.00","https://www.java.org"))
        list.add(Book("Python","15.00","https://www.python2.org"))
        listBook.setValue(list)
    }

    fun person(){
        person.setValue(PersonInfo(20,"lily","Washington DC Street 1100"))
    }

    fun asyncCall(){
        val async = Worker.asyncWork()
                async.async {
                    ALog.e("async 1")
                    AsyncResult("001","1")
                }
                .async {
                    ALog.e("async 2")
                    AsyncResult("002","2")
                }
                .async {
                    ALog.e("async 3")
                    AsyncResult("003","3")
                }
                .async {
                    ALog.e("async 4")
                    AsyncResult("004","4")
                }.done {
                    string.setValue("async:"+it.toString())
                }
    }

    fun syncCall(){
        val sync = Worker.syncWork()
                sync.sync {
                    it.toString().plus(",s1")
                 }
                .sync {
                    it.toString().plus(",s2")
                }
                .sync {
                    it.toString().plus(",s3")
                }
                .sync {
                    it.toString().plus(",s4")
                }
                .done {
                    string.setValue("sync:"+it.toString())
                }
    }
}