/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.uis.assignor.cache.CacheImpl
import com.uis.assignor.cache.ICache
import com.uis.assignor.utils.ALog
import com.uis.assignor.utils.TypeConvert
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * @autho uis
 * @date 2019-06-09
 * @github https://github.com/luiing
 */

object Assignor {

    @JvmStatic private var app: Application? = null
    @JvmStatic private val cache :ICache by lazy { CacheImpl(File(app!!.filesDir,".assignor")) }
    @JvmStatic private var observables = ConcurrentHashMap<Int, BodyStore>()

    @JvmStatic
    fun init(application: Application) {
        app ?: {
            synchronized(this) {
                app ?: {
                    app = application
                    application.registerActivityLifecycleCallbacks(object : ActLifecycle() {
                        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                            activity?.hashCode()?.apply {
                                stateChange(this, State_Created)
                            }
                        }

                        override fun onActivityResumed(activity: Activity?) {
                            activity?.hashCode()?.apply {
                                stateChange(this, State_Resumed)
                            }
                        }

                        override fun onActivityPaused(activity: Activity?) {
                            activity?.hashCode()?.apply {
                                stateChange(this, State_Paused)
                            }
                        }

                        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                            activity?.hashCode()?.apply {
                                stateChange(this, State_SaveInstance)
                            }
                        }

                        override fun onActivityDestroyed(activity: Activity?) {
                            activity?.hashCode()?.apply {
                                stateChange(this, State_Destroy)
                            }
                        }
                    })
                }()
            }
        }()
    }

    internal fun stateChange(code: Int, state: Int) {
        getStore(code).apply {
            onStateChanged(state)
            if (State_Destroy == state) {
                observables.remove(code)
            }
        }
        //ALog.e("BodyStore size is ${observables.size}")
    }

    @Synchronized internal fun getStore(code: Int): BodyStore{
        var store = observables[code]
        if(null == store){
            store = BodyStore()
            observables[code] = store
        }
        return store
    }

    @JvmStatic
    fun<T:BodyModel> of(activity: Activity,f:(T)->Unit):T {
        activity.application?.apply {
            init(this)
        }
        return of(activity.hashCode(),f)
    }

    /**
     * @param code see [Activity.hashCode]
     */
    @JvmStatic
    fun<T:BodyModel> of(code:Int,f:(T)->Unit):T = getStore(code).get(f)

    @JvmStatic
    fun cache(parent : File): ICache = CacheImpl(parent)

    @JvmStatic
    fun cache(): ICache = cache

    @JvmStatic fun<T> parseJson(content:String?,f:(T)->Unit):T {
        return if(TypeConvert.convert(f) == String::class.java) content as T
               else parseJson(content?.let{ JsonParser().parse(content)},f)
    }

    @JvmStatic fun<T> parseJson(element: JsonElement?, f:(T)->Unit):T{
        val type = TypeConvert.convert(f)
        return if(element == null){
            null as T
        }else if(type == null || type == String::class.java){
            element.toString() as T
        }else {
            Gson().fromJson(element, type)
        }
    }
}
