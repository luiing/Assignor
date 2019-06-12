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

    private data class StateObservable(var code: Int, var observable: AssignorAgent?)
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
                                _stateChange(this, State_Created)
                            }
                        }

                        override fun onActivityResumed(activity: Activity?) {
                            activity?.hashCode()?.apply {
                                _stateChange(this, State_Resumed)
                            }
                        }

                        override fun onActivityPaused(activity: Activity?) {
                            activity?.hashCode()?.apply {
                                _stateChange(this, State_Paused)
                            }
                        }

                        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                            activity?.hashCode()?.apply {
                                _stateChange(this, State_SaveInstance)
                            }
                        }

                        override fun onActivityDestroyed(activity: Activity?) {
                            activity?.hashCode()?.apply {
                                _stateChange(this, State_Destroy)
                            }
                        }
                    })
                }
            }
        }
    }

    private fun _stateChange(code: Int, state: Int) {
        observables[code]?.observable?.apply {
            onStateChanged(state)
            if (State_Destroy == state) {
                observables.remove(code)
            }
        }
    }

    private fun _init(code: Int): AssignorAgent{
        return observables[code]?.observable ?:  {obs: AssignorAgent->
            observables[code] =  StateObservable(code, obs)
            obs
        }(AssignorAgent())
    }

    @JvmStatic
    fun attach(activity: Activity): AssignorAgent {
        activity.apply {
            init(application)
        }
        return _init(activity.hashCode())
    }

    /**
     * @param code see [Activity.hashCode]
     */
    @JvmStatic
    fun agent(code:Int):AssignorAgent?{
        return observables[code]?.observable
    }

    @JvmStatic
    fun<T:AssignorOwner> createOwner(cls :Class<T>) :T{
        return cls.newInstance()
    }
}
