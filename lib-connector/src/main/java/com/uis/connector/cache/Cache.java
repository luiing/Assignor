package com.uis.connector.cache;

import java.io.File;
/**
 * @author uis
 */
public interface Cache {
    int NO_TIME_OUT = -1;
    String NO_PREFIX = "";
    /**
     * 获取内存缓存
     * @param key
     * @return json
     */
    String get(String key);

    /**
     * 获取内存缓存
     * @param key
     * @param mills 缓存时效（毫秒）
     * @return json
     */
    String get(String key, long mills);

    /**
     * 写入内存缓存
     * @param key
     * @param entity
     */
    void put(String key, Object entity);

    void put(String key, Object entity,long mills);

    /**
     * 写入磁盘缓存
     * @param key
     * @param value
     * @return
     */
    boolean writeFile(String key, Object value);

    /**
     * 读取磁盘缓存
     * @param key
     * @return json
     */
    String readFile(String key);

    /**
     * 读取磁盘缓存
     * @param key
     * @param mills 缓存时效（毫秒）
     * @return json
     */
    String readFile(String key, long mills);

    String readFile(String key, long mills,boolean saveCache);

    /**
     * 清除内存缓存
     * @param key
     */
    void clearMemory(String key);

    /**
     * 清除prefix前缀内存缓存
     */
    void clearAllMemory(String prefix);

    /**
     * 清除内存和文件缓存
     * @param key
     */
    void clear(String key);

    /**
     * 清除prefix前缀内存和文件缓存
     */
    void clearAll(String prefix);

    /**
     * 删除文件
     * @param path 文件路径
     */
    void deleteFile(String path);

    /**
     * 复制文件
     * @param res 源文件
     * @param des 目标文件
     * @return
     */
    boolean copyFile(File res,File des);

    /**
     * 保存到文件
     * @param isAppend true追加模式，false覆盖模式
     * @param data 数据
     * @param file 要保存的文件
     */
    void saveFile(boolean isAppend,byte[]data,File file);

    byte[] getFile(File file);
}
