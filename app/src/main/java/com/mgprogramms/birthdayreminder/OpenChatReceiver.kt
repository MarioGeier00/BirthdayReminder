package com.mgprogramms.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import com.mgprogramms.birthdayreminder.birthday.Contacts.Companion.getContactNameByIndex
import com.mgprogramms.birthdayreminder.birthday.Contacts.Companion.getPhoneNumberByContactId
import java.net.URLEncoder

const val CONTACT_ID = "contactId"

class OpenChatReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val contactId = intent.extras?.get(CONTACT_ID) as Int?
        if (contactId != null) {
            val phoneNumber = getPhoneNumberByContactId(context, contactId)
            val name = getContactNameByIndex(context, contactId)

            val message = "Hallo $name! Ich wünsche dir alles Gute zu deinem Geburtstag!"
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${URLEncoder.encode(message, "UTF-8")}"
            try {
                context.packageManager.getPackageInfo("com.whatsapp", PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()))
                val newActivity = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(context, newActivity, null)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    context,
                    "Whatsapp is not installed on your phone.",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }
}
