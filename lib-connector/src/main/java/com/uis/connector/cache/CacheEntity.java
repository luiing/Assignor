package com.uis.connector.cache;


import com.google.gson.Gson;
import com.uis.connector.comm.Clog;

/**
 * @author uis
 */
final class CacheEntity{
    public String result;
    public long mills;

    public CacheEntity(Object result, long mills) {
        if(result instanceof String){
            this.result = (String)result;
        }else {
            try {
                this.result = new Gson().newBuilder().disableHtmlEscaping().create().toJson(result);
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        }
        this.mills = mills;
    }
}
