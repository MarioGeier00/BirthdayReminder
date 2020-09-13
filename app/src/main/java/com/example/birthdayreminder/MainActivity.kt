package com.example.birthdayreminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contactListView = findViewById<ListView>(R.id.contactList)
        contactListView.setOnItemClickListener { adapterView, view, i, l ->

            val contactId = getContactIdByIndex(i)

            val title = getContactNameByIndex(i) + " hat Geburtstag"
            val message = ""

            setNotification(Calendar.getInstance().timeInMillis + 5000, this@MainActivity, title, message)

            true
        }
        contactListView.setOnItemLongClickListener { adapterView, view, i, l ->
            val contactId = getContactIdByIndex(i)
            if (contactId != null) {
                showEditContact(contactId)
            }
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    loadContactList()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                loadContactList()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS
                )
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadContactList() {

        val contactsCursor = getContacts();

        val data = arrayOf(R.id.contactName, R.id.birthday)

        if (contactsCursor !== null) {

            var adapter = SimpleCursorAdapter(
                this,
                R.layout.fragment_contact_list_item,
                contactsCursor,
                arrayOf("display_name", "data1"),
                data.toIntArray(),
                FLAG_REGISTER_CONTENT_OBSERVER
            )
            var listview = findViewById<ListView>(R.id.contactList)
            listview.adapter = adapter
            adapter.viewBinder = SimpleCursorAdapter.ViewBinder { a: View, b: Cursor, c: Int ->
                setViewValue(
                    a as TextView, b, c
                )
            }
        }
    }

    private fun showEditContact(contactId: Int) {
        val intent = Intent(Intent.ACTION_EDIT)
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI,
            contactId.toString()
        )
        intent.data = uri
        this.startActivity(intent)
    }

    private fun showContactDetail(contactId: Int) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI,
            contactId.toString()
        )
        intent.data = uri
        this.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContactIdByIndex(index: Int): Int? {
        val contactsCursor = getContacts()

        if (contactsCursor !== null) {
            contactsCursor.move(index + 1);

            val idColumnIndex = contactsCursor.getColumnIndex("contact_id")

            val text = contactsCursor.getInt(idColumnIndex)
            return text
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContactNameByIndex(index: Int): String? {
        val contactsCursor = getContacts()

        if (contactsCursor !== null) {
            contactsCursor.move(index + 1);

            val idColumnIndex = contactsCursor.getColumnIndex("display_name")

            val text = contactsCursor.getString(idColumnIndex)
            return text
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContacts(): Cursor? {

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
        val profileCursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            where,
            selectionArgs,
            null
        )

        return profileCursor
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setViewValue(
        aView: TextView,
        aCursor: Cursor,
        aColumnIndex: Int
    ): Boolean {
        if (aColumnIndex === 2) {
            val createDate: String = aCursor.getString(aColumnIndex)

            try {
                val date = LocalDate.parse(createDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val newDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

                aView.text = newDate
                return true
            } catch (ex: Exception) {

                //try {
                val date2 = LocalDate.parse(
                    "2020-" + createDate.removeRange(0, 2),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
                val newDate2 = date2.format(DateTimeFormatter.ofPattern("dd. MMMM"))

                aView.text = newDate2
                return true
//                } catch (ex: Exception) {
//
//                     return false
//                }
            }
        }
        return false
    }

}