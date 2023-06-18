package com.example.LockPro.ui.app_lock_private

import androidx.lifecycle.viewModelScope
import com.example.LockPro.base.BaseViewModel
import com.example.LockPro.model.AppLock
import com.example.LockPro.repository.Repository
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