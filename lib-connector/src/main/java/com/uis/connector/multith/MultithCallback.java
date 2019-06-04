package com.uis.connector.multith;

/**
 * @author uis
 */
public interface MultithCallback {
    /**
     * 子线程运行，多线程回调集合，集合为Map类型
     * @param response
     */
    void onMultith(MultithResponse response);

    /**
     * 子线程运行
     * @param position 编号
     * @param key 键名
     * @param value 键值
     */
    void onProgress(int position,String key,Object value);
}
