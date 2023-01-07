package de.mgprogramms.birthdayremindr.models

import android.database.Cursor
import android.provider.ContactsContract


data class Contact(
    val id: Int,
    val date: String,
    val name: String,
)


// Sets the columns to retrieve for the user profile
val ContactProjection = arrayOf(
    ContactsContract.Data.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Event._ID,
    ContactsContract.CommonDataKinds.Event.START_DATE,
    ContactsContract.CommonDataKinds.Event.CONTACT_ID,
)

fun Cursor.parseContact() =
    Contact(
        getInt(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.CONTACT_ID)),
        getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.START_DATE)),
        getString(getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME)),
    )
