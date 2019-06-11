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
interface ISyncWork {
    fun sync(work :(Any?)->Any) :ISyncWork

    fun syncSize(size :Int) :ISyncWork

    fun done(work :(Any?)->Unit)
}