package com.uis.connector.cache;

import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.uis.connector.comm.Clog;
import com.uis.connector.comm.ConnPlant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author uis
 */
public final class CacheImpl implements Cache {

    static final int maxSize = 200;
    private File parent;
    private LruCache<String,ReadWriteLock> locks;
    private LruCache<String,CacheEntity> lruCache;

    public CacheImpl(){
        this(null);
    }

    public CacheImpl(File directory){
        this(maxSize,directory);
    }

    public CacheImpl(int maxSize,File directory) {
        if(maxSize <= Cache.NO_TIME_OUT){
            maxSize = this.maxSize;
        }
        try {
            if (directory != null && directory.isDirectory()) {
                directory.mkdirs();
            }
            parent = directory;
        }catch (Throwable ex){
            Clog.printStack("Create Directory failed.");
            ex.printStackTrace();
        }
        lruCache = new InnerCache(maxSize);
        locks = new InnerLock();
    }

    static class InnerLock extends LruCache<String,ReadWriteLock>{
        public InnerLock() {
            super(20);
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, ReadWriteLock oldValue, ReadWriteLock newValue) {
            if(evicted && oldValue!=null){
                oldValue = null;
            }
        }

        @Override
        protected int sizeOf(String key, ReadWriteLock value) {
            return value==null ? 0 : 1;
        }
    }

    static class InnerCache extends LruCache<String,CacheEntity>{
        public InnerCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, CacheEntity oldValue, CacheEntity newValue) {
            if(evicted && oldValue!=null){
                oldValue.result = null;
            }
        }

        @Override
        protected int sizeOf(String key, CacheEntity value) {
            return value==null ? 0 : 1;
        }
    }

    private File getFilePath(String key){
        File file = null;
        if( null == parent && ConnPlant.app() != null){
            parent = new File(ConnPlant.app().getFilesDir(),"caches");
            if(!parent.exists()){
                parent.mkdirs();
            }
        }
        String alis = ConnPlant.MD5.encode(key);
        if(parent != null && !TextUtils.isEmpty(alis)){
            StringBuilder builder = new StringBuilder();
            for(int index=0;index < 3;index++){
                String dir = alis.substring(2*index,2*(index+1));
                builder.append(dir).append(File.separatorChar);
            }
            File absPath;
            String path = builder.toString();
            if(!TextUtils.isEmpty(path)){
                absPath = new File(parent,path);
                if(!absPath.exists()){
                    absPath.mkdirs();
                }
            }else{
                absPath = parent;
            }
            file = new File(absPath,key);
        }
        return file;
    }

    @Override
    public void clearMemory(String key) {
        lruCache.remove(key);
    }

    @Override
    public void clearAllMemory(String prefix) {
        if(TextUtils.isEmpty(prefix)){
            lruCache.evictAll();
        }else {
            Set<String> keys = lruCache.snapshot().keySet();
            for (String key : keys) {
                if (key.startsWith(prefix)) {
                    lruCache.remove(key);
                }
            }
            keys.clear();
        }
    }

    @Override
    public void clear(String key) {
        clearMemory(key);
        deleteFile(key);
    }

    @Override
    public void clearAll(String prefix) {
        clearAllMemory(prefix);
        if(parent != null) {
            deleteFiles(parent,prefix);
        }
    }

    private void deleteFiles(File root,String prefix){//递归删除
        try {
            File file;
            for (String name : root.list()) {
                file = new File(root,name);
                if(file.isDirectory()){
                    deleteFiles(file,prefix);
                    file.delete();
                }else if (TextUtils.isEmpty(prefix) || name.startsWith(prefix)) {
                    file.delete();
                }
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String get(String key){
        return get(key,Cache.NO_TIME_OUT);
    }

    @Override
    public String get(String key,long mills){
        String result = null;
        try{
            CacheEntity entity = TextUtils.isEmpty(key) ? null : lruCache.get(key);
            if(entity != null){
                if (mills <= 0 || (System.currentTimeMillis() - entity.mills) < mills) {
                    result = entity.result;
                }else{
                    lruCache.remove(key);
                }
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public void put(String key, Object entity) {
        put(key,entity,System.currentTimeMillis());
    }

    @Override
    public void put(String key,Object value,long mills){
        if(!TextUtils.isEmpty(key)) {
            if(null == value){
                clearMemory(key);
            }else {
                CacheEntity entity = new CacheEntity(value, mills);
                if(!TextUtils.isEmpty(entity.result)){
                    lruCache.put(key, entity);
                }else{
                    clearMemory(key);
                }
            }
        }
    }

    private ReadWriteLock getLock(String key){
        ReadWriteLock lock = locks.get(key);
        if(lock == null){
            lock = new ReentrantReadWriteLock();
            locks.put(key,lock);
        }
        return lock;
    }

    @Override
    public boolean writeFile(String key, Object value){
        boolean isSuccess = false;
        if(!TextUtils.isEmpty(key)) {
            ReadWriteLock lock = getLock(key);
            lock.writeLock().lock();
            try {
                File file = getFilePath(key);
                if(value == null){
                    file.delete();
                }else{
                    String json = new Gson().newBuilder().disableHtmlEscaping().create().toJson(new CacheEntity(value, System.currentTimeMillis()));
                    FileOutputStream fileOut = new FileOutputStream(file);
                    fileOut.write(json.getBytes());
                    fileOut.flush();
                    fileOut.close();
                    isSuccess = true;
                }
            }catch (Throwable ex) {
                Clog.printStackTrace(ex);
            }
            lock.writeLock().unlock();
        }
        return isSuccess;
    }

    @Override
    public String readFile(String key){
        return readFile(key,Cache.NO_TIME_OUT);
    }

    @Override
    public String readFile(String key,long mills){
        return readFile(key,mills,false);
    }

    @Override
    public String readFile(String key,long mills,boolean saveCache){
        String value = null;
        if(!TextUtils.isEmpty(key)) {
            ReadWriteLock lock = getLock(key);
            lock.readLock().lock();
            try {
                File file = getFilePath(key);
                FileInputStream fileIn = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileIn.read(bytes);
                fileIn.close();
                String result = new String(bytes, Charset.forName("UTF-8"));
                CacheEntity entity = new Gson().fromJson(result, CacheEntity.class);
                if(entity != null) {
                    if (mills <= 0 || (System.currentTimeMillis() - entity.mills) < mills) {
                        value = entity.result;
                    }
                }
                if(saveCache){
                    long currentMills = value == null ? System.currentTimeMillis():entity.mills;
                    put(key, value, currentMills);
                }
            }catch (Throwable ex) {
                ex.printStackTrace();
            }
            lock.readLock().unlock();
        }
        return value;
    }

    @Override
    public void deleteFile(String key){
        if(!TextUtils.isEmpty(key)) {
            try {
                File file = getFilePath(key);
                file.delete();
            } catch (Throwable ex) {
                Clog.printStackTrace(ex);
            }
        }
    }

    @Override
    public boolean copyFile(File res, File des) {
        boolean success = true;
        FileChannel channelRes = null;
        FileChannel channelDes = null;
        try {
            channelRes = new RandomAccessFile(res, "rwd").getChannel();
            channelDes = new RandomAccessFile(des, "rwd").getChannel();
            channelRes.transferTo(0, res.length(), channelDes);
        }catch (Throwable ex){
            ex.printStackTrace();
            success = false;
        }
        try{
            if(channelRes != null){
                channelRes.close();
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        try{
            if(channelDes != null){
                channelDes.close();
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }

        return success;
    }

    @Override
    public void saveFile(boolean isAppend, byte[] data, File file) {
        FileChannel channel = null;
        try {
            if(!file.exists()){
                File path = file.getParentFile();
                if(!path.exists()){
                    path.mkdirs();
                }
            }
            if(!isAppend && file.exists()){
                file.delete();
            }
            channel = new RandomAccessFile(file, "rwd").getChannel();
            channel.lock();
            if(isAppend) {
                channel.position(file.length());
            }
            int size = data.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            byteBuffer.put(data,0,size);
            byteBuffer.flip();
            channel.write(byteBuffer);
            byteBuffer.clear();
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        try{
            if(channel != null) {
                channel.close();
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }

    }

    @Override
    public byte[] getFile(File file) {
        FileChannel channel = null;
        byte[] data = null;
        try {
            if(file.exists()){
                channel = new RandomAccessFile(file, "rwd").getChannel();
                channel.lock();
                int len = (int)file.length();
                data = new byte[len];
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int total = 0;
                while ((len = channel.read(byteBuffer)) != -1){
                    byteBuffer.flip();
                    byteBuffer.get(data,total,len);
                    total += len;
                    byteBuffer.clear();
                }
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        try{
            if(channel != null){
                channel.close();
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        return data;
    }
}
