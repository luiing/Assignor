package com.uis.assignor

interface AssignorObserver<T> {
    fun onDataChanged(data :T)
}