package com.mgprogramms.birthdayreminder.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mgprogramms.birthdayreminder.BirthdayNotificationWorker

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val _notifications = MutableLiveData<Boolean>().apply {
        value = BirthdayNotificationWorker.isActivated(getApplication<Application>().applicationContext)
    }
    val notifications: LiveData<Boolean> = _notifications
}