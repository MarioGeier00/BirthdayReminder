package de.mgprogramms.birthdayreminder.providers

import android.content.Context

class NextBirthdayProvider(context: Context) {
    private val contactsProvider = BirthdayContactsProvider(context)
    fun getSortedBirthdays() =
        contactsProvider.getBirthdayContacts()
            .toMutableList()
            .also { it.sortBy { it.daysUntilBirthday } }

    fun getNextBirthdays() = getSortedBirthdays()
        .let { it.filter { contact -> it.first().daysUntilBirthday == contact.daysUntilBirthday } }

    fun getNextBirthdaysExceptToday() = getSortedBirthdays()
        .filter { it.daysUntilBirthday != 0L }
        .let { it.filter { contact -> it.first().daysUntilBirthday == contact.daysUntilBirthday } }
}
