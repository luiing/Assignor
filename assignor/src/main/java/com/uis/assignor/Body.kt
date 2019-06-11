package com.uis.assignor

import android.support.v4.util.ArrayMap
import com.uis.assignor.works.Worker

open class Body<T :Any>{
    companion object{
        @JvmStatic private val NONE = Any()
        @JvmStatic private val VERSION_NONE = -1
    }

    private data class ItemObserver<I>(var version:Int= VERSION_NONE,var owner: AssignorOwner,var observer: BodyObserver<I>)

    private val observers = ArrayMap<BodyObserver<T>,ItemObserver<T>>()
    @Volatile private var mState = State_Created
    @Volatile private var postValue = NONE
    private val postLock = Any()
    private var destroyCall :()->Unit = {}
    private var value :Any = NONE
    private var mVersion = VERSION_NONE

    @Suppress("UNCHECKED_CAST")
    private val postCall :()->Unit = {
        val v:Any
        synchronized(postLock){
            v = postValue
            postValue = NONE
        }
        setValue(v as T)
    }

    fun onStateChange(state :Int){
        this.mState = state
        when(state){
            State_Destroy-> {
                destroyCall()
                removeObservers()
            }
            State_Resumed->{
                _notifyDataChanged()
            }else-> {

            }
        }
    }

    fun onDestroy(call :()->Unit){
        destroyCall = call
    }

    fun setValue(v :T){
        if(Worker.isMainThread()) {
            value = v
            mVersion++
            _notifyDataChanged()
        }else{
            val canPost:Boolean
            synchronized(postLock){
                canPost = postValue == NONE
                postValue = v
            }
            if(canPost) {
                Worker.mainExecute(postCall)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun _notifyDataChanged(){
        if(State_Resumed == this.mState){
            for (item in observers.values){
                if(item.version < mVersion && State_Resumed == this.mState){
                    item.version = mVersion
                    item.observer.onBodyChanged(value as T)
                }
            }
        }
    }

    fun addObserver(owner: AssignorOwner,observer: BodyObserver<T>){
        if(!observers.containsKey(observer)){
            observers[observer] = ItemObserver(owner = owner,observer = observer)
        }
    }

    private fun removeObservers(){
        observers.clear()
    }
}