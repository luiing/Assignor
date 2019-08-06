/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/** 获取对象范性Type
 * @author uis
 */
object TypeConvert {

    @JvmStatic
    fun convert(target: Any): Type? = convertAll(target)?.let { it[0] }

    @JvmStatic
    fun convertAll(target: Any): Array<Type>? {
        var result: Array<Type>? = null
        val cls = target.javaClass
        val types = cls.genericInterfaces
        if (types.isNotEmpty()) {
            result = convertType(types[0])
        }
        return result ?: cls.genericSuperclass?.let {
            convertType(it)
        }
    }

    @JvmStatic
    private fun convertType(type: Type): Array<Type>? {
        var result: Array<Type>? = null
        if (type is ParameterizedType) {
            result = type.actualTypeArguments
        }
        return result
    }
}
