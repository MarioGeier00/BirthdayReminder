package de.mgprogramms.birthdayremindr.providers

import android.content.Context
import android.provider.ContactsContract

class RawContactIdProvider(val context: Context) {

    private val where = "${ContactsContract.RawContacts.ACCOUNT_TYPE} = ? AND " +
            "${ContactsContract.RawContacts.CONTACT_ID} = ?"

    fun getWhatsAppRawContactIds(contactId: Int): MutableList<Long> {
        val cursor = context.contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts._ID),
            where,
            arrayOf("com.whatsapp", contactId.toString()),
            null
        )!!

        val rawContactIds = mutableListOf<Long>()
        val rawContactIdColumn = cursor.getColumnIndex(ContactsContract.RawContacts._ID)

        while (cursor.moveToNext()) {
            rawContactIds.add(cursor.getLong(rawContactIdColumn))
        }
        cursor.close()

        return rawContactIds
    }
}
