/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.callable

import com.uis.assignor.AssignorResult

/**
 * 观察者回调接口，范型T是回调结果的类型，根据T的类型配对
 * @author uis
 * @param <T>
*/
interface Assignor<T> {

    fun onCacheKey():String = ""

    fun onResult(resp: AssignorResult<T>)

    fun onCacheResult(resp: AssignorResult<T>)
}
