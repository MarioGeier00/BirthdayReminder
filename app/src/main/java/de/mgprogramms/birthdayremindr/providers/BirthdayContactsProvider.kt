package de.mgprogramms.birthdayreminder.providers

import android.content.Context
import de.mgprogramms.birthdayreminder.models.toBirthdayContact

class BirthdayContactsProvider(context: Context) {
    private val contactsProvider = ContactsProvider(context)
    fun getBirthdayContacts() = contactsProvider.getContacts().map { it.toBirthdayContact() }
}
