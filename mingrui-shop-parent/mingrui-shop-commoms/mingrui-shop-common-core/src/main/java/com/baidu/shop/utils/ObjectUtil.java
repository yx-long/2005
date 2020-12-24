package com.baidu.shop.utils;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class ObjectUtil {

    public static Boolean isNull(Object obj){
        return obj == null;
    }

    public static Boolean isNotNull(Object obj){
        return obj != null;
    }
}
