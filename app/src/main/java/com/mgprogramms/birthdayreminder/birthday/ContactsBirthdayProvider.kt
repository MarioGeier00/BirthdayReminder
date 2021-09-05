package com.mgprogramms.birthdayreminder.birthday

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.mgprogramms.birthdayreminder.parseDate

class ContactsBirthdayProvider
    (context: Context) : BirthdayProvider(context) {

    private val contactsCursor: Cursor

    private val nameIndex: Int
    private val birthdayIndex: Int
    private val idIndex: Int

    init {
        // Sets the columns to retrieve for the user profile
        val projection = arrayOf(
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Event._ID,
            ContactsContract.CommonDataKinds.Event.START_DATE,
            ContactsContract.CommonDataKinds.Event.CONTACT_ID,
        )

        val where =
            ContactsContract.Data.MIMETYPE + "= ? AND " +
            ContactsContract.CommonDataKinds.Event.TYPE + "= ?"

        val selectionArgs: Array<String> = arrayOf(
            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString()
        )

        contactsCursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            where,
            selectionArgs,
            ContactsContract.CommonDataKinds.Event.START_DATE + " ASC"
        )!!

        nameIndex = contactsCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)
        birthdayIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE)
        idIndex = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID)
    }

    override fun getBirthdays(): Array<BirthdayData> {
        val birthdays = emptyList<BirthdayData>().toMutableList()
        contactsCursor.moveToFirst()
        while (contactsCursor.moveToNext()) {
            val name = contactsCursor.getString(nameIndex)
            val birthday = contactsCursor.getString(birthdayIndex)
            val idIndex = contactsCursor.getInt(idIndex)
            birthdays.add(BirthdayData(parseDate(birthday), name, idIndex.toString()))
        }
        return birthdays.toTypedArray()
    }

}
