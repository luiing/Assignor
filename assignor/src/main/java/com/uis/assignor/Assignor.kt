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
import com.uis.assignor.utils.ALog

/**
 * @autho uis
 * @date 2019-06-09
 * @github https://github.com/luiing
 */

object Assignor {

    private data class StateObservable(var code: Int, var store: BodyStore?)
    private var app: Application? = null
    private var observables = ArrayMap<Int, StateObservable>()

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
        observables[code]?.store?.apply {
            onStateChanged(state)
            if (State_Destroy == state) {
                observables.remove(code)
            }
        }
    }

    internal fun init(code: Int): BodyStore{
        return observables[code]?.store ?:  {store: BodyStore->
            observables[code] =  StateObservable(code, store)
            store
        }(BodyStore())
    }

    @JvmStatic
    fun of(activity: Activity): BodyStore {
        activity.application?.apply {
            init(this)
            ALog.e("Assignor init")
        }
        return init(activity.hashCode())
    }

    /**
     * @param code see [Activity.hashCode]
     */
    @JvmStatic
    fun of(code:Int):BodyStore?{
        return observables[code]?.store
    }

    @JvmStatic
    fun<T:BodyModel> createModel(cls :Class<T>) :T{
        return cls.newInstance()
    }
}
