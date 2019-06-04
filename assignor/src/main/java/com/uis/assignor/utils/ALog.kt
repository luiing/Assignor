/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.utils

import android.util.Log

object ALog {

    private var debug = false

    fun enableLog() {
        debug = true
    }

    fun printStackTrace(ex: Throwable) {
        if (debug) {
            ex.printStackTrace()
        }
    }

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
        print(builder.append(msg).toString())
    }

    fun print(msg: String) {
        if (!debug) {
            return
        }
        val size = msg.length
        val length = 2048
        if (size <= length) {
            Log.e("ALog", msg)
        } else {
            var i = 0
            var start = i
            while (i < size) {
                i = if (i + length < size) i + length else size
                Log.e("ALog", msg.substring(start, i))
                start = i
            }
        }
    }

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
        print(builder.toString())
    }
}
