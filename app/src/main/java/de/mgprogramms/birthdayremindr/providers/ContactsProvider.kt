package de.mgprogramms.birthdayremindr.providers

import android.content.Context
import android.provider.ContactsContract
import de.mgprogramms.birthdayremindr.models.Contact
import de.mgprogramms.birthdayremindr.models.ContactProjection
import de.mgprogramms.birthdayremindr.models.parseContact

class ContactsProvider(val context: Context) {

    private val where = "${ContactsContract.Data.MIMETYPE} = ? AND " +
            "${ContactsContract.CommonDataKinds.Event.TYPE} = ${ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY}"

    private val selectionArgs = arrayOf(
        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
    )

    fun getContacts(): MutableList<Contact> {
        // Retrieves the profile from the Contacts Provider
        val cursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            ContactProjection,
            where,
            selectionArgs,
            "${ContactsContract.CommonDataKinds.Event.START_DATE} ASC"
        )!!

        val contacts = mutableListOf<Contact>()

        while (cursor.moveToNext()) {
            contacts.add(cursor.parseContact())
        }
        cursor.close()

        return contacts
    }

    fun getContactById(contactId: Int): Contact? {
        val cursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            ContactProjection,
            "$where AND ${ContactsContract.CommonDataKinds.Event.CONTACT_ID} = ?",
            selectionArgs.plus(contactId.toString()),
            null
        )!!

        val result = if (cursor.moveToFirst()) {
            cursor.parseContact()
        } else {
            null
        }
        cursor.close()

        return result
    }

}
