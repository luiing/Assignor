/*
 * Copyright (c) 2020 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.decouple;

import java.lang.reflect.Field;

public class BindCoupleUtils {

    public  static String generatePkg = "a.b.c.d.generate";
    public  static String generate = "Generate";

    public static Object getBindCoupleValue(String bindName){
        String clsName = generatePkg + "." + bindName + generate;
        try {
            Class<?> cls = Class.forName(clsName);
            Field field = cls.getField(bindName);
            field.setAccessible(true);
            return field.get(cls.newInstance());
        }catch (Throwable ex){
            System.out.println("------ Not found "+clsName+" ------");
        }
        return null;
    }
}
