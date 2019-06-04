package com.uis.connector.workshop;

import android.app.Activity;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.uis.connector.callback.ActLifecycle;
import com.uis.connector.comm.Clog;
import com.uis.connector.comm.ConnPlant;
import com.uis.connector.comm.TypeConvert;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * support auto unregister when attach activity
 * @author uis
 */
public class ConnObserverOwner{
    private List<ConnObserver> obs = new ArrayList<>(4);
    private List<Type> types = new ArrayList<>(4);
    private Activity activity;
    private ConnRelease connRelease;
    private final Object lock = new Object();

    private static List<ConnObserverOwner> sOwnerList = new ArrayList<>(4);
    static{
        initActLifecycle();
    }

    private static void initActLifecycle(){
        if(ConnPlant.app() != null){
            ConnPlant.app().registerActivityLifecycleCallbacks(new ActLifecycle() {
                @Override
                public void onActivityDestroyed(Activity activity) {
                    ConnObserverOwner item;
                    for(int i = sOwnerList.size()-1;i>=0;--i){
                        item = sOwnerList.get(i);
                        if(item != null && item.activity!=null && item.activity == activity){
                            sOwnerList.remove(i);
                            item.unregisterObservers();
                        }
                    }
                }
            });
        }
    }

    public ConnObserverOwner attachActivity(Activity activity){
        if(ConnPlant.app() == null && activity!=null){
            ConnPlant.setApp(activity.getApplication());
            initActLifecycle();
        }
        if(ConnPlant.app() != null && activity!=null) {
            this.activity = activity;
            if(!sOwnerList.contains(this)) {
                sOwnerList.add(this);
            }
        }
        return this;
    }

    public ConnObserverOwner attachFragment(android.app.Fragment fragment){
        if(fragment != null && fragment.getActivity() != null) {
            attachActivity(fragment.getActivity());
        }
        return this;
    }

    public ConnObserverOwner attachFragment(Fragment fragment){
        if(fragment != null && fragment.getActivity() != null) {
            attachActivity(fragment.getActivity());
        }
        return this;
    }

    public void setConnRelease(ConnRelease release){
        connRelease = release;
    }

    public void notifyCache(String cacheKey){
        notifyCache(cacheKey,-1);
    }

    public void notifyCache(String cacheKey,long cacheMills){
        notifyCache(cacheKey,cacheMills,false);
    }

    /**
     * 读取存储
     * @param cacheKey
     * @param cacheMills
     * @param isMemory true:内存，false:内存没有取磁盘缓存
     */
    public void notifyCache(String cacheKey, long cacheMills,boolean isMemory){
        ConnPlant.submit(new CacheReadRunnable(this,cacheKey,cacheMills,isMemory));
    }

    private void notifyCacheCall(String cacheKey,String result){
        if(!TextUtils.isEmpty(cacheKey)) {
            synchronized (lock) {
                Type type;
                ConnObserver connObs;
                for (int i = obs.size() - 1; i >= 0; --i) {
                    type = types.get(i);
                    connObs = obs.get(i);
                    if (type != null && TextUtils.equals(connObs.getCacheKey(),cacheKey)) {
                        notifyCallback( connObs, Response.newBuilder(new Gson().fromJson(result,type))
                                .setCacheKey(cacheKey).setCache()
                                .build());
                    }
                }
            }
        }
    }

    public void notifyResponse(@NonNull Response resp){
        if(resp.result != null) {
            String cacheKey = resp.cacheKey;
            if (!TextUtils.isEmpty(cacheKey)) {
                ConnPlant.writeCache(cacheKey, resp.result);
            }
            synchronized (lock) {
                Type type;
                ConnObserver connObs;
                for (int i = obs.size() - 1; i >= 0; --i) {
                    type = types.get(i);
                    connObs = obs.get(i);
                    boolean canObserver = !TextUtils.isEmpty(cacheKey) && TextUtils.equals(connObs.getCacheKey(), cacheKey);
                    if (!canObserver) {
                        Class resClass = resp.result.getClass();
                        canObserver = type != null && ( type.equals(resClass) || type.equals(resClass.getGenericSuperclass()) );
                    }

                    if (canObserver) {
                        notifyCallback(connObs, resp);
                    }
                }
            }
        }
    }

    /**
     * 清除内存缓存
     * @param cacheKey
     */
    public void clearMemoryCache(String cacheKey){
        ConnPlant.clearMemoryCache(cacheKey);
    }

    /**
     * 清除内存和磁盘缓存
     * @param cacheKey
     */
    public void clearCache(String cacheKey){
        ConnPlant.clearCache(cacheKey);
    }

    private void notifyCallback( ConnObserver observer,@NonNull Response resp){
        if(obs != null && obs.contains(observer) && resp.result != null) {
            ConnPlant.handler().post(new CallbackRunnable(observer, resp));
        }
    }

    public void registerObserver(ConnObserver... observers){
        registerObserver(true,-1,observers);
    }

    public void registerObserver(boolean isMemeory,long cacheMills,ConnObserver... observers){
        synchronized(lock) {
            for(ConnObserver observer : observers) {
                if (!obs.contains(observer)) {
                    obs.add(observer);
                    types.add(TypeConvert.convert(observer));
                }
                String key = observer.getCacheKey();
                if (!TextUtils.isEmpty(key)) {
                    notifyCache(key, cacheMills, isMemeory);
                }
            }
        }
    }

    public void unregisterObserver(ConnObserver... observers){
        synchronized(lock) {
            for(ConnObserver observer : observers) {
                if (obs.contains(observer)) {
                    int index = obs.indexOf(observer);
                    if (index >= 0) {
                        obs.remove(index);
                        types.remove(index);
                    }
                }
            }
        }
    }

    public void unregisterObservers(){
        synchronized (lock) {
            obs.clear();
            types.clear();
        }
        activity = null;
        if(connRelease != null){
            connRelease.onRelease();
        }
        connRelease = null;
    }

    public static boolean isMainLooper(){
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private static class CallbackRunnable implements Runnable{
        ConnObserver observer;
        Response resp;

        private CallbackRunnable(ConnObserver observer, Response resp) {
            this.observer = observer;
            this.resp = resp;
        }

        @SuppressWarnings("All")
        @Override
        public void run() {
            try {
                if(resp.isCache){
                    observer.onCacheResponse(resp);
                }else {
                    observer.onResponse(resp);
                }
            }catch (Throwable ex){
                Clog.printStackTrace(ex);
            }
        }
    }

    private static class CacheReadRunnable implements Runnable{

        ConnObserverOwner owner;
        boolean isMemory;
        String cacheKey;
        long mills;

        private CacheReadRunnable(ConnObserverOwner owner, String cacheKey, long mills, boolean isMemory) {
            this.owner = owner;
            this.isMemory = isMemory;
            this.cacheKey = cacheKey;
            this.mills = mills;
        }

        @Override
        public void run() {
            try {
                owner.notifyCacheCall(cacheKey, ConnPlant.readCache(isMemory, cacheKey, mills));
            }catch (Throwable ex){
                Clog.printStackTrace(ex);
            }
        }
    }
}
