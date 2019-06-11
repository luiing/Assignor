/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.utils

import android.util.Log

object ALog {

    @JvmStatic
    private var debug = false
    private var priority = Log.DEBUG

    @JvmStatic
    fun enableLog(priority: Int = Log.DEBUG) {
        this.priority = priority
        this.debug = true
    }

    @JvmStatic
    fun printStackTrace(ex: Throwable) {
        if (debug) {
            ex.printStackTrace()
        }
    }

    @JvmStatic
    fun printStack(msg: String) {
        if (!debug) {
            return
        }
        val builder = StringBuilder("")
        val elements = Thread.currentThread().stackTrace
        var element: StackTraceElement
        var i = 3
        val cnt = elements.size
        while (i < cnt) {
            element = elements[i]
            builder.append(element.className).append("(")
                    .append(element.lineNumber).append(")")
                    .append(element.methodName).append("()\n")
            i++
        }
        this.print(Log.ERROR,builder.append(msg).toString())
    }

    @JvmStatic
    fun printStack() {
        if (!debug) {
            return
        }
        val builder = StringBuilder()
        for (element in Thread.currentThread().stackTrace) {
            builder.append(element.className).append("(")
                    .append(element.lineNumber).append(")")
                    .append(element.methodName).append("()\n")
        }
        this.print(Log.ERROR,builder.toString())
    }

    @JvmStatic
    fun v(msg :String){
        print(Log.VERBOSE,msg)
    }

    @JvmStatic
    fun d(msg :String){
        print(Log.DEBUG,msg)
    }

    @JvmStatic
    fun i(msg :String){
        print(Log.INFO,msg)
    }

    @JvmStatic
    fun w(msg :String){
        print(Log.WARN,msg)
    }

    @JvmStatic
    fun e(msg :String){
        print(Log.ERROR,msg)
    }

    @JvmStatic
    fun print(priority :Int,msg: String) {
        if (!debug || this.priority > priority) {
            return
        }
        val size = msg.length
        val length = 2048
        if (size <= length) {
            Log.println(priority,"ALog", msg)
        } else {
            var i = 0
            var start = i
            while (i < size) {
                i = if (i + length < size) i + length else size
                Log.println(priority,"ALog", msg.substring(start, i))
                start = i
            }
        }
    }
}
