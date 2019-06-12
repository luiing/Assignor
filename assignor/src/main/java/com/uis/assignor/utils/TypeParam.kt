/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.utils

import java.lang.reflect.Type

/**
 * @autho uis
 * @date 2019-06-12
 * @github https://github.com/luiing
 */
open class TypeParam<T> {
    fun getType() :Type? = TypeConvert.convert(this)
}