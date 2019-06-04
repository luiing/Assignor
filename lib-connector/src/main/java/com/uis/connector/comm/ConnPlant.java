package com.uis.connector.comm;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.uis.connector.cache.Cache;
import com.uis.connector.cache.CacheImpl;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author uis
 */
final public class ConnPlant {
    private static Application application;
    private static ExecutorService mService;
    private static ThreadPoolExecutor mPoolExecutor;
    private final static int coreSize = Math.min(12,2*Runtime.getRuntime().availableProcessors()+1);
    private final static int maxNumSize = 2*coreSize;
    private final static int capacitySize = 256;
    private static ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(@Nullable Runnable r) {
            String name = "Connector Pool #" + mCount.getAndIncrement();
            Clog.print(name);
            return new Thread(r, name);
        }
    };
    static {
        mPoolExecutor = new ThreadPoolExecutor(coreSize,maxNumSize,30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(capacitySize),sThreadFactory);
        mService = mPoolExecutor;
        try {//7.0有些手机，8.0手机获取为null
            Method method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
            application  = (Application) method.invoke(null);
        }catch (Throwable ex) {
            Clog.printStackTrace(ex);
        }
    }
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static Cache cache = new CacheImpl();

    public static boolean isPoolIdel(){
        return (capacitySize - mPoolExecutor.getQueue().remainingCapacity()) < coreSize ;
    }

    public static ExecutorService getService() {
        return mService;
    }

    public static void submit(final Runnable... runnables){
        if(runnables != null){
            for(Runnable run:runnables){
                try {
                    mService.submit(run);
                }catch (Throwable ex){
                    Clog.printStackTrace(ex);
                }
            }
        }
    }

    public static Handler handler() {
        return mHandler;
    }

    public static Application app() {
        return application;
    }

    public static void setApp(@NonNull Application app) {
        if(application == null) {
            application = app;
        }
    }

    public static Cache cache(){
        return cache;
    }



    static String getFileName(String prefix,String key){
        return prefix + MD5.encode(key);
    }

    public static void clearAll(String prefix){
        cache.clearAll(prefix);
    }

    public static void clearAllMemory(String prefix){
        cache.clearAllMemory(prefix);
    }

    public static void clearMemoryCache(String key){
        clearMemoryCache(Cache.NO_PREFIX,key);
    }

    public static void clearMemoryCache(String prefix,String key){
        if(!TextUtils.isEmpty(key)) {
            cache.clearMemory(getFileName(prefix,key));
        }
    }

    public static void clearCache(String key){
        clearCache(Cache.NO_PREFIX,key);
    }

    public static void clearCache(String prefix,String key){
        if(!TextUtils.isEmpty(key)) {
            cache.clear(getFileName(prefix,key));
        }
    }

    public static void writeCache(String key, final Object value){
        writeCache(false,Cache.NO_PREFIX,key,value);
    }

    public static void writeCache(String prefix,String key, final Object value){
        writeCache(false,prefix,key,value);
    }

    public static void writeCache(boolean isMemory, String prefix,String key, final Object value){
        if(!TextUtils.isEmpty(key)) {
            final String kk = getFileName(prefix,key);
            cache.put(kk, value);
            if(!isMemory) {
                submit(new Runnable() {
                    @Override
                    public void run() {
                        cache.writeFile(kk, value);
                    }
                });
            }
        }
    }

    public static String readCache(String key){
        return readCache(false,Cache.NO_PREFIX,key,Cache.NO_TIME_OUT);
    }

    public static String readCache(String key, long mills){
        return readCache(false,Cache.NO_PREFIX,key,mills);
    }

    public static String readCache(String prefix,String key){
        return readCache(false,prefix,key,Cache.NO_TIME_OUT);
    }

    public static String readCache(String prefix,String key, long mills){
        return readCache(false,prefix,key,mills);
    }

    public static String readCache(boolean isMemory,String key,long mills){
        return readCache(isMemory,Cache.NO_PREFIX,key,mills);
    }

    public static String readCache(boolean isMemory,String prefix,String key,long mills){
        String value = null;
        if(!TextUtils.isEmpty(key)) {
            final String kk = getFileName(prefix,key);
            value = cache.get(kk, mills);
            if (!isMemory && value == null) {
                value = cache.readFile(kk, mills,true);
            }
        }
        return value;
    }

    public static class MD5{
        private static final char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'
        };
        public static byte[] encodeByte(byte[] bytes) throws NoSuchAlgorithmException {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            int byteCount=bytes.length;
            digester.update(bytes, 0, byteCount);
            return digester.digest();
        }

        public static String encode(String str){
            return encode(str.getBytes());
        }

        public static String encode(byte[] bytes){
            String result = "";
            try {
                byte[] res= encodeByte(bytes);
                char cha[] = new char[32];
                int k = 0;
                for (int i = 0; i < 16; i++){
                    byte temp = res[i];
                    cha[k++] = hexDigits[temp >>> 4 & 0xf];
                    cha[k++] = hexDigits[temp & 0xf];
                }
                result = new String(cha);
            } catch (Exception ex) {
                Clog.printStackTrace(ex);
            }
            return result;
        }
    }
}
