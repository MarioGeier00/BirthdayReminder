package com.mgprogramms.birthdayreminder.birthday

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.annotation.RequiresApi


class Contacts {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun getContactIdByIndex(context: Context, index: Int): Int? {
            val contactsCursor = getContacts(context)

            if (contactsCursor !== null) {
                return getContactIdByIndex(contactsCursor, index)
            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getContactIdByIndex(contactsCursor: Cursor, index: Int): Int {
            contactsCursor.moveToPosition(index)
            val idColumnIndex = contactsCursor.getColumnIndex("contact_id")
            return contactsCursor.getInt(idColumnIndex)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun getContactNameByIndex(context: Context, index: Int): String? {
            val contactsCursor = getContacts(context)

            if (contactsCursor !== null) {
                return getContactNameByIndex(contactsCursor, index)
            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getContactNameByIndex(contactsCursor: Cursor, index: Int): String? {
            contactsCursor.moveToPosition(index)
            val idColumnIndex = contactsCursor.getColumnIndex("display_name")
            return contactsCursor.getString(idColumnIndex)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun getBirthdayByIndex(context: Context, index: Int): String? {
            val contactsCursor = getContacts(context)

            if (contactsCursor !== null) {
                return getBirthdayByIndex(contactsCursor, index)
            }
            return null
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getBirthdayByIndex(contactsCursor: Cursor, index: Int): String? {
            contactsCursor.moveToPosition(index)
            val birthdayColumnIndex = contactsCursor.getColumnIndex("data1")
            return contactsCursor.getString(birthdayColumnIndex)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun getContacts(context: Context): Cursor? {

            // Sets the columns to retrieve for the user profile
            val projection = arrayOf(
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event._ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
            )

            val where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY

            val selectionArgs: Array<String> = arrayOf(
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
            )

            return try {
                // Retrieves the profile from the Contacts Provider
                context.contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    projection,
                    where,
                    selectionArgs,
                    ContactsContract.CommonDataKinds.Event.START_DATE + " ASC"
                )
            } catch (e: SecurityException) {
                null
            }

        }

        fun getPhoneNumberByContactId(context: Context, contactId: Int): String? {
            val whatsAppContacts = context.contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                arrayOf(ContactsContract.RawContacts._ID),
                ContactsContract.RawContacts.ACCOUNT_TYPE + " = ? AND " + ContactsContract.RawContacts.CONTACT_ID + " = ?",
                arrayOf("com.whatsapp", contactId.toString()),
                null
            )

            if (whatsAppContacts != null && whatsAppContacts.moveToFirst()) {

                val rawContactIdColumn = whatsAppContacts.getColumnIndex(ContactsContract.RawContacts._ID)
                if (rawContactIdColumn >= 0) {
                    val rawContactId = whatsAppContacts.getInt(rawContactIdColumn)

                    val whatsAppNumber = context.contentResolver.query(
                        Phone.CONTENT_URI,
                        arrayOf(Phone.NUMBER),
                        Phone.CONTACT_ID + " = ? AND " + Phone.RAW_CONTACT_ID + " = ?",
                        arrayOf(contactId.toString(), rawContactId.toString()), null
                    )

                    if (whatsAppNumber != null && whatsAppNumber.moveToFirst()) {
                        val numberColumn = whatsAppNumber.getColumnIndex(Phone.NUMBER)
                        if (numberColumn >= 0) {
                            val number = whatsAppNumber.getString(numberColumn)
                            whatsAppNumber.close()
                            return number
                        }
                    }
                }
                whatsAppContacts.close()
            }

            return null
        }

    }
}
