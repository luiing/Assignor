package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.ALog
import com.uis.assignor.works.Worker

open class BodyData<T :Any> :IState{
    companion object{
        @JvmStatic private val VERSION_NONE = -1
    }

    private data class ItemObserver<I>(var version:Int= VERSION_NONE,var observer: (I)->Unit)

    private val postLock = Any()
    private val observers = ArrayMap<((T)->Unit),ItemObserver<T>>()
    @Volatile private var mState = State_Created
    @Volatile private var postValue :Any? = null
    @Volatile private var mValue :Any? = postValue
    @Volatile private var mVersion = VERSION_NONE
    
    @Suppress("UNCHECKED_CAST")
    private val postCall :()->Unit = {
        synchronized(postLock){
            val v = postValue
            postValue = null
            (v as? T)?.apply {
                setValue(this)
            }
            return@synchronized
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
     * @return 获取缓存value
     */
    @Suppress("unchecked_cast")
    fun getValue() :T?{
        return mValue as? T
    }

    fun setValue(v :T){
        if(Worker.isMainThread()) {
            mVersion++
            mValue = v
            kotlin.runCatching{
                notifyDataChanged()
            }.exceptionOrNull()?.printStackTrace()
        }else{
            val canPost:Boolean
            synchronized(postLock){
                canPost = postValue == null
                postValue = v
            }
            if(canPost) {
                Worker.mainExecute(postCall)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun notifyDataChanged(){
        if(State_Resumed == this.mState){
            mValue?.apply {
                for (item in observers.values) {
                    if (item.version < mVersion && State_Resumed == mState) {
                        item.version = mVersion
                        item.observer(this as T)
                    }
                }
            }
        }
    }

    fun addObserver(observer: (T)->Unit){
        if(!observers.containsKey(observer)){
            observers[observer] = ItemObserver(observer = observer)
        }
    }

    internal fun removeObservers(){
        observers.clear()
    }
}