package com.mgprogramms.birthdayreminder.ui.home

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mgprogramms.birthdayreminder.getBirthdayByIndex
import com.mgprogramms.birthdayreminder.getContactNameByIndex
import com.mgprogramms.birthdayreminder.getContacts
import com.mgprogramms.birthdayreminder.parseDate
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private fun getContext(): Context = getApplication<Application>().applicationContext

    private val _nameOfNextBirthday = MutableLiveData<String>()

    private val _daysUntilNextBirthday = MutableLiveData<Int>().apply {
        value = getDaysUntilNextBirthday()
    }


    private fun getDaysUntilNextBirthday(): Int? {
        val contacts = getContacts(getContext())

        if (contacts != null) {

            val calendar = Calendar.getInstance()

            var savedIndex = -1
            var minDays = Int.MAX_VALUE

            for (i in 0 until contacts.count) {
                val date = getBirthdayByIndex(contacts, i)
                if (date != null) {
                    val parsedDate = parseDate(date).parsedDate
                    var currentDate = calendar.time.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    val birthdayInCurrentYear =
                        LocalDate.of(currentDate.year, parsedDate.monthValue, parsedDate.dayOfMonth)

                    if (birthdayInCurrentYear < currentDate) {
                        currentDate = currentDate.minusYears(1)
                    }

                    val daysUntilBirthday = ChronoUnit.DAYS.between(
                        currentDate, birthdayInCurrentYear
                    )

                    if (daysUntilBirthday < minDays) {
                        savedIndex = i
                        minDays = daysUntilBirthday.toInt()
                    }
                }
            }

            if (savedIndex >= 0) {
                val contactName = getContactNameByIndex(
                    getContext(),
                    savedIndex
                )
                if (contactName != null) {
                    _nameOfNextBirthday.value = contactName!!
                }
                return minDays
            }
        }
        return null
    }

    val nameOfNextBirthday: LiveData<String> = _nameOfNextBirthday
    val daysUntilNextBirthday: LiveData<Int> = _daysUntilNextBirthday
}