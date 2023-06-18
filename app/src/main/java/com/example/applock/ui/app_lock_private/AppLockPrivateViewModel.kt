package com.example.applock.ui.app_lock_private

import androidx.lifecycle.viewModelScope
import com.example.applock.base.BaseViewModel
import com.example.applock.model.AppLock
import com.example.applock.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppLockPrivateViewModel : BaseViewModel() {
    private val response: Repository = Repository()
    fun update(appLock: AppLock, onSuccessors: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                async {
                    response.update(appLock)
                }.await()
                withContext(Dispatchers.Main) {
                    onSuccessors.invoke()
                }

            } catch (e: Throwable) {

            }
        }
    }
}