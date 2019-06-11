package com.uis.assignor

interface BodyObserver<T> {
    fun onBodyChanged(data :T)
}