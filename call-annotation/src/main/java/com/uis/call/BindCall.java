/*
 * Copyright (c) 2020 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.call;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface BindCall {
    String value() default "";
}