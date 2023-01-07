package de.mgprogramms.birthdayremindr.providers

import android.content.Context
import android.provider.ContactsContract

class PhoneNumberProvider(val context: Context) {

    private val where = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ? AND " +
            "${ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID} = ?"

    fun getPhoneNumbersByRawContactId(contactId: Int, rawContactId: Long): MutableList<String> {
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            where,
            arrayOf(contactId.toString(), rawContactId.toString()),
            null
        )!!

        val contacts = mutableListOf<String>()
        val phoneNumberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while (cursor.moveToNext()) {
            contacts.add(cursor.getString(phoneNumberColumn))
        }
        cursor.close()

        return contacts
    }
}
