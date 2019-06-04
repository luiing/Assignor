package com.uis.connector.multith;

import android.support.v4.util.ArrayMap;
import com.uis.connector.comm.ConnPlant;
import com.uis.connector.workshop.ConnObserverOwner;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程并发任务回调
 * @author uis
 */

public class MultithOwner {

    private final Multith multith;
    private MultithCallback callback;

    public MultithOwner(int count,MultithCallback callback) {
        this.multith = new Multith(count);
        this.callback = callback;
    }

    public void setResult(final String key,final Object value){
        if(ConnObserverOwner.isMainLooper()){
            ConnPlant.submit(new Runnable() {
                @Override
                public void run() {
                    setResultCallback(key,value);
                }
            });
        }else{
            setResultCallback(key, value);
        }
    }

    private void setResultCallback(String key, Object value){
        try {
            if (key != null) {
                int cnt = multith.multiResponse(key, value);
                if (callback != null) {
                    int index = multith.size() - cnt;
                    callback.onProgress(index, key, value);
                }
                if (0 == cnt) {
                    if (callback != null) {
                        callback.onMultith(new MultithResponse(multith.getResult()));
                    }
                    multith.reset();
                }
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
    }


    static class Multith{
        private final int count;
        private final Lock lock;

        private AtomicInteger cnt;
        private Map<String,Object> resultMap;

        Multith(int count) {
            lock = new ReentrantLock();
            this.count = count;
            resultMap = new ArrayMap<>();
            cnt = new AtomicInteger(count);
        }

        Map<String,Object> getResult(){
            return resultMap;
        }

        int size(){
            return count;
        }

        int multiResponse(String key,Object value){
            lock.lock();
            resultMap.put(key, value);
            lock.unlock();
            return cnt.decrementAndGet();
        }

        void reset(){
            cnt.getAndSet(count);
            resultMap.clear();
        }
    }
}
