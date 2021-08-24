package com.mgprogramms.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

const val CONTACT_ID = "contactId"

class OpenChatReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val contactId = intent.extras?.get(CONTACT_ID) as Int?
        if (contactId != null) {
            val phoneNumber = getPhoneNumberByContactId(context, contactId)

            val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
            try {
                context.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val newActivity = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(context, newActivity, null)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(
                    context,
                    "Whatsapp is not installed in your phone.",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }
}