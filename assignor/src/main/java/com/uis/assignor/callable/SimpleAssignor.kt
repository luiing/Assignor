/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.callable

import com.uis.assignor.AssignorResult

abstract class SimpleAssignor<T> : Assignor<T> {

    override val cacheKey: String get() = ""

    override fun onResult(resp: AssignorResult<T>) {

    }

    override fun onCacheResult(resp: AssignorResult<T>) {

    }
}
