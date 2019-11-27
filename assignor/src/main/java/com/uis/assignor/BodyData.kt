package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.utils.ALog
import com.uis.assignor.works.Worker

open class BodyData<T :Any> :IState{
    companion object{
        @JvmStatic private val VERSION_NONE = -1
        @JvmStatic private val VERSION_ONCE = -2
        @JvmStatic private val VALUE_NONE = Any()
    }

    private data class ItemObserver<T>(var version:Int,var observer: (T)->Unit)
    private val observers = ArrayMap<Int,ItemObserver<T>>()
    @Volatile private var mState = State_Created
    @Volatile private var postValue :Any = VALUE_NONE
    private var mValue :Any = VALUE_NONE
    private var mVersion = VERSION_NONE
    
    @Suppress("UNCHECKED_CAST")
    private val postCall :()->Unit = {
        val v :Any = postValue
        postValue = VALUE_NONE
        setValue(v as T)
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

    /** 清除缓存值*/
    @Synchronized fun clearValue(){
        mValue = VALUE_NONE
    }

    @Synchronized private fun notifyValue(){
        if(mValue != VALUE_NONE && State_Resumed == this.mState) {
            if (Worker.isMainThread()) {
                notifyDataChanged()
            } else {
                Worker.mainExecute({
                    notifyDataChanged()
                })
            }
        }
    }

    /** value更新，回调总是在主线程 */
    @Synchronized fun setValue(v :T){
        if(Worker.isMainThread()) {
            mVersion++
            mValue = v
            notifyDataChanged()
        }else{
            postValue = v
            Worker.mainExecute(postCall)
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun notifyDataChanged(){
        if(State_Resumed == this.mState && mValue != VALUE_NONE && observers.size > 0){
            kotlin.runCatching{
                mValue.apply {
                    val it = observers.values.iterator()
                    while (it.hasNext()){
                        val item = it.next()
                        if (item.version < mVersion && State_Resumed == mState) {
                            item.observer(this as T)
                            if(VERSION_ONCE == item.version){
                                it.remove()
                            }else{
                                item.version = mVersion
                            }
                        }
                    }
                }
            }.exceptionOrNull()?.printStackTrace()
        }
    }

    fun observer(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(VERSION_NONE,observer)
        }
        notifyValue()
    }

    fun observerStartNow(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(mVersion,observer)
        }
    }

    fun observerOnce(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(VERSION_ONCE,observer)
        }
        notifyValue()
    }

    fun observerOnceStartNow(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(mVersion,observer)
        }
    }

    fun removeObserver(observer: (T)->Unit){
        observers.remove(observer.hashCode())
    }

    fun removeObservers(){
        observers.clear()
    }
}