package com.mgprogramms.birthdayreminder.birthday

import android.os.Build
import com.mgprogramms.birthdayreminder.BirthDate
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class BirthdayData(val birthDate: BirthDate, val name: String, val id: String) {

    private var _daysUntilNextBirthday: Long? = null

    fun daysUntilNextBirthday(): Long {
        if (_daysUntilNextBirthday === null) {

            var currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            val birthdayInCurrentYear =
                LocalDate.of(currentDate.year, birthDate.parsedDate.monthValue, birthDate.parsedDate.dayOfMonth)

            if (birthdayInCurrentYear < currentDate) {
                currentDate = currentDate.minusYears(1)
            }
            _daysUntilNextBirthday = ChronoUnit.DAYS.between(currentDate, birthdayInCurrentYear)

        }

        return _daysUntilNextBirthday as Long
    }

}
