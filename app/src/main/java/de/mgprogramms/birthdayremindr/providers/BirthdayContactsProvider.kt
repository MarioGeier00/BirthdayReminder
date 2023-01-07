package de.mgprogramms.birthdayremindr.providers

import android.content.Context
import de.mgprogramms.birthdayremindr.models.toBirthdayContact

class BirthdayContactsProvider(context: Context) {
    private val contactsProvider = ContactsProvider(context)
    fun getBirthdayContacts() = contactsProvider.getContacts().map { it.toBirthdayContact() }
}
