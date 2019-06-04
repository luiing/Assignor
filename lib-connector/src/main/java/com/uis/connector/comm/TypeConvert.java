package com.uis.connector.comm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/** 获取对象范性Type
 * @author uis
 */
public class TypeConvert {

    public static Type convert(Object target){
        Type[] result = convertAll(target);
        return result != null ? result[0] : null;
    }

    public static Type[] convertAll(Object target){
        Type result[] = null;
        if(target != null) {
            Class cls = target.getClass();
            Type[] types = cls.getGenericInterfaces();
            if (types.length > 0) {
                result = convertType(types[0]);
            }
            if (result == null) {
                result = convertType(cls.getGenericSuperclass());
            }
        }
        return result;
    }

    private static Type[] convertType(Type type) {
        Type[] result = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] params = paramType.getActualTypeArguments();
            List<Type> list = new ArrayList<>();
            if (params.length > 0) {
                int index = 0;
                for(Type t : params) {
                    list.add(t);
                    ++index;
                }
                if(index > 0) {
                    result = new Type[index];
                    list.toArray(result);
                }
            }
        }
        return result;
    }
}
