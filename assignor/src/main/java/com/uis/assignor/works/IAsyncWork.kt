/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.works
/**
 * @autho uis
 * @date 2019-06-07
 * @github https://github.com/luiing
 */
interface IAsyncWork {
    fun async(work :()->AsyncResult) :IAsyncWork

    fun asyncSize(size :Int) :IAsyncWork

    fun done(work :(MutableList<AsyncResult>)->Unit)

}