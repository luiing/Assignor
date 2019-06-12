package com.uis.assignor

import android.support.annotation.MainThread

interface BodyObserver<T> {
    @MainThread
    fun onDataChanged(data :T)
}