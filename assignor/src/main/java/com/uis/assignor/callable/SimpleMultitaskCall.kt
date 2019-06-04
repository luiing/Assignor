/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.callable

import com.uis.assignor.case.MultitaskCase

abstract class SimpleMultitaskCall : MultithtaskCall {
    override fun onProgress(position: Int, key: String, value: Any) {

    }

    override fun onMultitask(response: MultitaskCase) {

    }
}
