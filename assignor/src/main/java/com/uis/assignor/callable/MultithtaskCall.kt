/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.callable

import com.uis.assignor.case.MultitaskCase

interface MultithtaskCall {
    /**
     * 子线程运行，多线程回调集合，集合为Map类型
     * @param response
     */
    fun onMultitask(response: MultitaskCase)

    /**
     * 子线程运行
     * @param position 编号
     * @param key 键名
     * @param value 键值
     */
    fun onProgress(position: Int, key: String, value: Any)
}
