package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.ALog
import com.uis.assignor.works.Worker

open class BodyData<T :Any> :IState{
    companion object{
        @JvmStatic private val VERSION_NONE = -1
        @JvmStatic private val VALUE_NONE = Any()
    }

    private data class ItemObserver<I>(var version:Int= VERSION_NONE,var observer: (I)->Unit)

    private val postLock = Any()
    private val observers = ArrayMap<((T)->Unit),ItemObserver<T>>()
    @Volatile private var mState = State_Created
    @Volatile private var postValue :Any = VALUE_NONE
    private var mValue :Any = VALUE_NONE
    private var mVersion = VERSION_NONE
    
    @Suppress("UNCHECKED_CAST")
    private val postCall :()->Unit = {
        val v :Any
        synchronized(postLock){
             v = postValue
            postValue = VALUE_NONE
            return@synchronized
        }
        (v as? T)?.apply {
            setValue(v)
        }
    }

    override fun onStateChanged(state: Int) {
        this.mState = state
        when(state){
            State_Destroy-> {
                removeObservers()
            }
            State_Resumed->{
                notifyDataChanged()
            }
        }
    }

    /**
     * @return 同步获取缓存value
     */
    @Suppress("unchecked_cast")
    fun getValue() :T?{
        if(mValue != VALUE_NONE){
            return mValue as T
        }
        return null
    }

    /** 异步取值，已经回调的值不会在回调 */
    fun asyncValue(){
        synchronized(postLock) {
            notifyDataChanged()
        }
    }

    /** value更新，回调总是在主线程 */
    fun setValue(v :T){
        if(Worker.isMainThread()) {
            mVersion++
            mValue = v
            notifyDataChanged()
        }else{
            synchronized(postLock){
                postValue = v
            }
            Worker.mainExecute(postCall)
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun notifyDataChanged(){
        if(State_Resumed == this.mState && mValue != VALUE_NONE && observers.size > 0){
            kotlin.runCatching{
                mValue.apply {
                    for (item in observers.values) {
                        if (item.version < mVersion && State_Resumed == mState) {
                            item.version = mVersion
                            item.observer(this as T)
                        }
                    }
                }
            }.exceptionOrNull()?.printStackTrace()
        }
    }

    fun observer(observer: (T)->Unit){
        if(!observers.containsKey(observer)){
            observers[observer] = ItemObserver(observer = observer)
        }
    }

    fun removeObserver(observer: (T)->Unit){
        observers.remove(observer)
    }

    fun removeObservers(){
        observers.clear()
    }
}