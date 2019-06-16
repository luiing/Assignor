/*
 * Copyright (c) 2019 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.assignor

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.util.ArrayMap
import com.uis.assignor.cache.CacheImpl
import com.uis.assignor.cache.ICache
import com.uis.assignor.utils.ALog
import java.io.File

/**
 * @autho uis
 * @date 2019-06-09
 * @github https://github.com/luiing
 */

object Assignor {

    @JvmStatic private var app: Application? = null
    @JvmStatic private val cache :ICache by lazy { CacheImpl(File(app!!.filesDir,".assignor")) }
    @JvmStatic private var observables = ArrayMap<Int, BodyStore>()

    @JvmStatic
    fun init(application: Application) {
        if (app == null) {
            synchronized(this) {
                if (app == null) {
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
                }
            }
        }
    }

    internal fun stateChange(code: Int, state: Int) {
        observables[code]?.apply {
            onStateChanged(state)
            if (State_Destroy == state) {
                observables.remove(code)
            }
        }
        //ALog.e("BodyStore size is ${observables.size}")
    }

    internal fun init(code: Int): BodyStore{
        return observables[code] ?:  {store: BodyStore->
            observables[code] =  store
            store
        }(BodyStore())
    }

    @JvmStatic
    fun of(activity: Activity): BodyStore {
        activity.application?.apply {
            init(this)
        }
        return init(activity.hashCode())
    }

    /**
     * @param code see [Activity.hashCode]
     */
    @JvmStatic
    fun of(code:Int):BodyStore{
        return init(code)
    }

    @JvmStatic
    fun<T:BodyModel> createModel(cls :Class<T>) :T{
        return cls.newInstance()
    }

    @JvmStatic
    fun cache(parent : File): ICache = CacheImpl(parent)

    @JvmStatic
    fun cache(): ICache = cache
}
