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

        BirthdayNotificationCreator().onReceive(this, null)

        val contactListView = findViewById<ListView>(R.id.contactList)
        contactListView.setOnItemClickListener { adapterView, view, i, l ->

            val contactId = getContactIdByIndex(this, i)

            val title = getContactNameByIndex(this, i) + " hat Geburtstag"
            val message = ""

            showNotification(this, title, message)

            true
        }
        contactListView.setOnItemLongClickListener { adapterView, view, i, l ->
            val contactId = getContactIdByIndex(this, i)
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

        val contactsCursor = getContacts(this);

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
    fun setViewValue(
        aView: TextView,
        aCursor: Cursor,
        aColumnIndex: Int
    ): Boolean {
        if (aColumnIndex === 2) {
            val createDate: String = aCursor.getString(aColumnIndex)

            val friendlyDate = createFriendlyDate(createDate)
            aView.text = friendlyDate
            return true
        }
        return false
    }

}