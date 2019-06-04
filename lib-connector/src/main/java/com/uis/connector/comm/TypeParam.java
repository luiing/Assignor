package com.uis.connector.comm;

import java.lang.reflect.Type;

/**
 * 获取范性Type
 * @author uis on 2018/4/27.
 */
@Deprecated
public class TypeParam<T> {
    public Type getType(){
        return TypeConvert.convert(this);
    }
}
