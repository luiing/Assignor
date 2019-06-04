package com.uis.connector.comm;

import android.util.Log;
/**
 * @author uis
 */
public class Clog {

    private static boolean debug = false;

    public static void enableLog(){
        debug = true;
    }

    public static void printStackTrace(Throwable ex){
        if(debug) {
            ex.printStackTrace();
        }
    }

    public static void printStack(String msg){
        if(!debug){
            return;
        }
        StringBuilder builder = new StringBuilder("");
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element;
        for(int i = 3,cnt = elements.length;i<cnt;i++) {
            element = elements[i];
            builder.append(element.getClassName()).append("(")
                    .append(element.getLineNumber()).append(")")
                    .append(element.getMethodName()).append("()\n");
        }
        print(builder.append(msg).toString());
    }

    public static void print(String msg){
        if(!debug) {
            return;
        }
        int size = msg.length();
        final int length = 2048;
        if(size <= length){
            Log.e("Clog",msg);
        }else{
            for(int i=0; i< size;){
                int start = i;
                i = (i+length < size) ? i+length : size;
                Log.e("Clog",msg.substring(start,i));
            }
        }
    }

    public static void printStack(){
        if(!debug) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for(StackTraceElement element:Thread.currentThread().getStackTrace()){
            builder.append(element.getClassName()).append("(")
                    .append(element.getLineNumber()).append(")")
                    .append(element.getMethodName()).append("()\n");
        }
        print(builder.toString());
    }
}
