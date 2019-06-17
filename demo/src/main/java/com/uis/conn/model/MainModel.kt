/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.model

import com.uis.assignor.BodyModel
import com.uis.assignor.BodyData
import com.uis.assignor.utils.ALog
import com.uis.conn.data.Book

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
class MainModel :BodyModel() {
    val book = BodyData<Book>()
    val string = BodyData<String>()
}