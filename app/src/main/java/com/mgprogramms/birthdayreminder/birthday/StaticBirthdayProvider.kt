package com.mgprogramms.birthdayreminder.birthday

import android.content.Context
import android.os.Build
import com.mgprogramms.birthdayreminder.BirthDate
import java.time.LocalDate

class StaticBirthdayProvider(context: Context, var data: Array<BirthdayData>) : BirthdayProvider(context) {
    override fun getBirthdays(): Array<BirthdayData> {
        return data
    }

    companion object {
        fun forToday(context: Context, name: String): BirthdayProvider {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                StaticBirthdayProvider(
                    context,
                    arrayOf(BirthdayData(BirthDate(LocalDate.now(), false), name, "StaticBirthdayProvider$name"))
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }
    }
}
