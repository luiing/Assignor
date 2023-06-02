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
import com.uis.assignor.utils.TypeConvert
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * @author uis
 * @date 2019-06-09
 * @github https://github.com/luiing
 */

object Assignor {
    @JvmStatic private var app: Application? = null
    @JvmStatic private val cache :ICache by lazy { CacheImpl(File(app!!.filesDir,".assignor")) }
    @JvmStatic private var lifeObservers = ConcurrentHashMap<Int, BodyStore>()
    @JvmStatic private var stableStore = BodyStore(State_Resumed)

    @JvmStatic
    fun init(application: Application) {
        app ?: synchronized(this) {
            app ?: run {
                app = application
                application.registerActivityLifecycleCallbacks(object : ActLifecycle() {
                    override fun onActivityCreated(
                        activity: Activity,
                        savedInstanceState: Bundle?
                    ) {
                        activity.hashCode().apply {
                            this@Assignor.stateChange(this, State_Created)
                        }
                    }

                    override fun onActivityResumed(activity: Activity) {
                        activity.hashCode().apply {
                            this@Assignor.stateChange(this, State_Resumed)
                        }
                    }

                    override fun onActivityPaused(activity: Activity) {
                        activity.hashCode().apply {
                            this@Assignor.stateChange(this, State_Paused)
                        }
                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                        activity.hashCode().apply {
                            this@Assignor.stateChange(this, State_SaveInstance)
                        }
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                        activity.hashCode().apply {
                            this@Assignor.stateChange(this, State_Destroy)
                        }
                    }
                })
            }
        }
    }

    internal fun stateChange(code: Int, state: Int) {
        getStore(code).apply {
            onStateChanged(state)
            if (State_Destroy == state) {
                lifeObservers.remove(code)
            }
        }
    }

    @Synchronized fun getStore(code: Int): BodyStore = lifeObservers[code] ?:
        BodyStore().apply {
            lifeObservers[code] = this
        }

    @JvmStatic
    fun<T:BodyModel> of(activity: Activity, f:(T)->Unit):T {
        activity.application?.apply {
            init(this)
        }
        return of(activity.hashCode(),f)
    }

    /** 同一个Activity只能有唯一个BodyDataContainer
     * @param code see [Activity.hashCode]
     */
    @JvmStatic
    fun<T:BodyModel> of(code:Int, f:(T)->Unit):T = getStore(code).get(f)

    /** 同一个Activity有一个name名称BodyDataContainer(也就是Activity有多个name不同的BodyDataContainer)
     * @param code see [Activity.hashCode]
     */
    @JvmStatic
    fun<T:BodyModel> of(code:Int, name:String, f:(T)->Unit):T = getStore(code).get(f,name)

    @JvmStatic
    fun<T:BodyModel> of(code:Int, cls:Class<T>):T = getStore(code).get(cls)

    @JvmStatic
    fun<T:BodyModel> of(code:Int, name:String, cls:Class<T>):T = getStore(code).get(cls,name)

    @JvmStatic
    fun<T:BodyModel> of(activity: Activity, cls:Class<T>):T = getStore(activity.hashCode()).get(cls)

    @JvmStatic
    fun<T:BodyModel> of(activity: Activity, name:String, cls:Class<T>):T = getStore(activity.hashCode()).get(cls,name)

    @JvmStatic
    fun getStore():BodyStore = stableStore

    @JvmStatic
    fun cache(parent : File): ICache = CacheImpl(parent)

    @JvmStatic
    fun cache(): ICache = cache

    @Suppress("UNCHECKED_CAST")
    @JvmStatic fun<T> parseJson(content:String?,f:(T)->Unit):T {
        return if(TypeConvert.convert(f) == String::class.java) content as T
               else parseJson(content?.let{ JsonParser.parseString(content)},f)
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic fun<T> parseJson(element: JsonElement?, f:(T)->Unit):T{
        return element?.let { _ ->
            TypeConvert.convert(f)?.let {
                if (it == String::class.java) {
                    element.toString() as T
                } else {
                    Gson().fromJson(element, it)
                }
            }
        } ?: (null as T)
    }
}
