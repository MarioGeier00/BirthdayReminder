package com.example.birthdayreminder

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
public fun getContactIdByIndex(context: Context, index: Int): Int? {
    val contactsCursor = getContacts(context)

    if (contactsCursor !== null) {
        contactsCursor.move(index + 1);

        val idColumnIndex = contactsCursor.getColumnIndex("contact_id")

        val text = contactsCursor.getInt(idColumnIndex)
        return text
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
public fun getContactNameByIndex(context: Context, index: Int): String? {
    val contactsCursor = getContacts(context)

    if (contactsCursor !== null) {
        contactsCursor.move(index + 1);

        val idColumnIndex = contactsCursor.getColumnIndex("display_name")

        val text = contactsCursor.getString(idColumnIndex)
        return text
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
public fun getContacts(context: Context): Cursor? {

    // Sets the columns to retrieve for the user profile
    val projection = arrayOf(
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Event._ID,
        ContactsContract.CommonDataKinds.Event.START_DATE,
        ContactsContract.CommonDataKinds.Event.CONTACT_ID
    )

    val where =
        ContactsContract.Data.MIMETYPE + "= ? AND " +
                ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;

    val selectionArgs: Array<String> = arrayOf(
        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
    )

    // Retrieves the profile from the Contacts Provider
    val profileCursor = context.contentResolver.query(
        ContactsContract.Data.CONTENT_URI,
        projection,
        where,
        selectionArgs,
        null
    )

    return profileCursor
}