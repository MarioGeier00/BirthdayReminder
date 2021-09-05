package com.mgprogramms.birthdayreminder.birthday

import android.content.Context

abstract class BirthdayProvider(context: Context) {
    abstract fun getBirthdays(): Array<BirthdayData>
}
