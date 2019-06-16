/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.viewmodel

import android.os.SystemClock
import android.text.TextUtils
import com.google.gson.Gson
import com.uis.assignor.Assignor
import com.uis.assignor.works.Worker
import com.uis.conn.data.Book
import com.uis.conn.model.MainModel

/**
 * @autho uis
 * @date 2019-06-16
 * @github https://github.com/luiing
 */

class MainViewModel(var view: MainView?){
    val model = Assignor.of(view.hashCode()).get(MainModel::class.java)
    init {
        model.destroy {
            view = null
        }
        model.book.observer {
            view?.displayContent("name:".plus(it.name).plus(",price:¥").plus(it.price)
                    .plus("\nwebsite:").plus(it.website))
        }
        model.string.observer {
            view?.displayContent("result:".plus(it))
        }
    }

    fun readMemoryBook(){
        val cache = Assignor.cache().readCache("book")
        if(!TextUtils.isEmpty(cache)) {
            model.book.setValue(Gson().fromJson(cache,Book::class.java))
        }
    }

    fun readBook(){
        val cache = Assignor.cache().readCache("book",isDisk = true)
        if(!TextUtils.isEmpty(cache)) {
            model.book.setValue(Gson().fromJson(cache,Book::class.java))
        }else{
            model.string.setValue("book is null")
        }
    }

    fun writeBook(){
        Worker.ioExecute {
            Assignor.cache().writeCache("book",Book("Java Book","22.00","https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=detail&fr=&hs=0&xthttps=111111&sf=1&fmq=1560693122772_R&pv=&ic=0&nc=1&z=&se=&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=%E7%B2%BE%E5%93%81%E7%BE%8E%E5%A5%B3&oq=%E7%B2%BE%E5%93%81%E7%BE%8E%E5%A5%B3&rsp=-1")
                    ,true)
            model.string.setValue("wirte success")
        }
    }

    fun removeMemoryCache(){
        Assignor.cache().removeCache("book")
        model.string.setValue("remove memory success")
    }

    fun getBooks(){
        Worker.ioExecute {
            SystemClock.sleep(1000)
            model.string.setValue("get 10 books success,total price is ¥220.00")
        }
    }
}