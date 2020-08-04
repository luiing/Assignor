package com.uis.assignor

import androidx.collection.ArrayMap
import com.uis.assignor.works.Worker

open class BodyData<T :Any> :IState{
    companion object{
        @JvmStatic private val VERSION_NONE = -1
        @JvmStatic private val VALUE_NONE = Any()
    }

    private data class ItemObserver<T>(var observer: (T)->Unit,var once:Boolean=false,var version:Int=VERSION_NONE)
    private val observers = ArrayMap<Int,ItemObserver<T>>()
    @Volatile private var mState = State_Created
    @Volatile private var mValue :Any = VALUE_NONE
    @Volatile private var mVersion = VERSION_NONE

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
            Worker.mainExecute({
                notifyDataChanged()
            })
        }
    }

    /** value更新，回调总是在主线程 */
    @Synchronized fun setValue(v :T){
        mVersion++
        mValue = v
        notifyValue()
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
                            if(item.once){
                                it.remove()
                            }else{
                                item.version = mVersion
                            }
                            item.observer(this as T)
                        }
                    }
                }
            }.exceptionOrNull()?.printStackTrace()
        }
    }

    fun observer(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(observer)
        }
        notifyValue()
    }

    fun observerStartNow(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(observer,false,mVersion)
        }
    }

    fun observerOnce(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(observer,true)
        }
        notifyValue()
    }

    fun observerOnceStartNow(observer: (T)->Unit){
        val code = observer.hashCode()
        if(!observers.containsKey(code)){
            observers[code] = ItemObserver(observer,true,mVersion)
        }
    }

    fun removeObserver(observer: (T)->Unit){
        observers.remove(observer.hashCode())
    }

    fun removeObservers(){
        observers.clear()
    }
}