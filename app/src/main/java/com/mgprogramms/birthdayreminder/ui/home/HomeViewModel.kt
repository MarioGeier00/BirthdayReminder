package com.mgprogramms.birthdayreminder.ui.home

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mgprogramms.birthdayreminder.birthday.BirthdayData
import com.mgprogramms.birthdayreminder.birthday.BirthdayProviderFactory

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private fun getContext(): Context = getApplication<Application>().applicationContext

    private val _nameOfNextBirthday = MutableLiveData<String>()

    private val _daysUntilNextBirthday = MutableLiveData<Long>().apply {
        value = getDaysUntilNextBirthday()
    }


    private fun getDaysUntilNextBirthday(): Long? {
        val birthdayProvider = BirthdayProviderFactory.buildProvider(getContext())

        val birthdays = birthdayProvider.getBirthdays()

        if (birthdays.isNotEmpty()) {
            var closestBirthday: BirthdayData = birthdays.first()
            for (birthday in birthdays) {
                if (birthday.daysUntilNextBirthday() < closestBirthday.daysUntilNextBirthday()) {
                    closestBirthday = birthday
                }
            }
            _nameOfNextBirthday.value = closestBirthday.name
            return closestBirthday.daysUntilNextBirthday()
        }

        return null
    }

    val nameOfNextBirthday: LiveData<String> = _nameOfNextBirthday
    val daysUntilNextBirthday: LiveData<Long> = _daysUntilNextBirthday
}
