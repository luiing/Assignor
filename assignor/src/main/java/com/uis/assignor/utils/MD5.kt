/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.utils

import java.security.MessageDigest


object MD5 {
    @JvmStatic
    private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    @JvmStatic
    fun _md5(bytes: ByteArray): ByteArray = MessageDigest.getInstance("MD5").digest(bytes)

    @JvmStatic
    fun md5(str: String): String = md5(str.toByteArray())

    @JvmStatic
    fun md5(bytes: ByteArray): String {
        var result = ""
        try {
            val res = _md5(bytes)
            val cha = CharArray(32)
            for (i in 0..15) {
                val temp = res[i].toInt()
                cha[2*i] = hexDigits[temp ushr 4 and 0xf]
                cha[2*i+1] = hexDigits[temp and 0xf]
            }
            result = String(cha)
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        return result
    }
}