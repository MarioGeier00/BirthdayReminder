package de.mgprogramms.birthdayremindr.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.SimPhonebookContract.SimRecords.PHONE_NUMBER
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import de.mgprogramms.birthdayremindr.R
import de.mgprogramms.birthdayremindr.providers.ContactsProvider
import java.net.URLEncoder

const val CONTACT_ID = "contactId"

class OpenChatReceiver : BroadcastReceiver() {

    @RequiresApi(33)
    override fun onReceive(context: Context, intent: Intent) {
        intent.extras?.getString(PHONE_NUMBER)
            ?.let { phoneNumber ->
                val contact = intent.extras?.getInt(CONTACT_ID)
                    ?.let { ContactsProvider(context).getContactById(it) }

                val message = context.getString(R.string.birthday_message, contact?.name)
                val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${URLEncoder.encode(message, "UTF-8")}"
                try {
                    context.packageManager.getPackageInfo(
                        "com.whatsapp",
                        PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                    )
                    val newActivity = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(context, newActivity, null)
                } catch (e: PackageManager.NameNotFoundException) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_whatsapp_not_installed),
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }
    }

}
