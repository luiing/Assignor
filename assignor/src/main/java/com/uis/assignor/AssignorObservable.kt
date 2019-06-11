package com.uis.assignor

class AssignorObservable{
    /** 0: destroy,1:initialize or create ,2:resume */
    @Volatile private var mState = State_Created
    private var destroyCall :()->Unit = {}
    //private var


    fun onStateChange(state :Int){
        this.mState = state
        destroyCall()
    }

    fun registerObserver(){

    }

    fun unregisterObservers(){

    }
}