package com.mgprogramms.birthdayreminder.birthday

import android.os.Build
import com.mgprogramms.birthdayreminder.BirthDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class BirthdayData(val birthDate: BirthDate, val name: String, val id: Int) {

    private var _daysUntilNextBirthday: Long? = null

    fun daysUntilNextBirthday(): Long {
        if (_daysUntilNextBirthday === null) {

            val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            var birthdayInCurrentYear =
                LocalDate.of(currentDate.year, birthDate.parsedDate.monthValue, birthDate.parsedDate.dayOfMonth)

            if (birthdayInCurrentYear < currentDate) {
                birthdayInCurrentYear = birthdayInCurrentYear.plusYears(1)
            }
            _daysUntilNextBirthday = ChronoUnit.DAYS.between(currentDate, birthdayInCurrentYear)

        }

        return _daysUntilNextBirthday as Long
    }

    fun hasBirthday(): Boolean {
        val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return currentDate.dayOfMonth == birthDate.parsedDate.dayOfMonth &&
                currentDate.monthValue == birthDate.parsedDate.monthValue
    }

}
