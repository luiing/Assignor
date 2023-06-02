/*
 * Copyright (c) 2021 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn.kv

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tencent.mmkv.MMKV
import com.uis.assignor.utils.ALog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

/**
 * @autho uis
 * @date 2021/4/29
 * @github https://github.com/luiing
 */

val Context.dataStore by preferencesDataStore(name = "settings")

class StorageManager  constructor(val context: Context) {

    val _times = 100

    val mmkv by  lazy {
        MMKV.defaultMMKV()!!
    }

    val sp by lazy {
        context.getSharedPreferences("sp",0)!!
    }

    suspend fun dsWrite(){
        var key: Preferences.Key<String>
        context.dataStore.edit {edit->
            edit.clear()
            repeat(_times){
                key = stringPreferencesKey("ds$it")
                edit[key] = "ds-${System.currentTimeMillis()}"
            }
        }
    }

    suspend fun getDataStore(){
        context.dataStore.data.map {
            it[stringPreferencesKey("ds999")]
        }.collectLatest {
            ALog.e("ds:"+it);
        }
    }

    suspend fun dsRead(){
        context.dataStore.data.map {
            it[stringPreferencesKey("ds")]?:""
        }
    }

    fun spWrite(){
            //sp.edit().clear().commit()
            repeat(_times){
                sp.edit().putString("sp$it","sp-${System.currentTimeMillis()}").commit()
            }

        ALog.e("sp:"+sp.getString("sp999",""))
    }

    fun spRead(){

            repeat(_times){
                sp.getString("sp$it","sp-$it")
            }
    }

    fun mmkvWrite(){
            mmkv.clearAll()
            repeat(_times){
                mmkv.encode("mmkv$it","mmkv-${System.currentTimeMillis()}")
            }
        ALog.e("mmkv:"+mmkv.decodeString("mmkv999"))

    }

    fun mmkvRead(){

            repeat(_times){
                mmkv.decodeString("mmkv_$it")
            }

    }
}