/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.proxy

object ProxyAssignor {
    fun <T :Any> proxyInstance(handler: ProxyHandler<T>): T? {
        return handler.proxy()
    }
}
