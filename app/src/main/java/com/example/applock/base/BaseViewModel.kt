package com.example.applock.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

abstract class BaseViewModel() : ViewModel() {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    val message: MutableLiveData<String> = MutableLiveData()

    private fun cleanUp() {
        scope.cancel()
    }


    override fun onCleared() {
        super.onCleared()
        cleanUp()
    }
}
