package com.mgprogramms.birthdayreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi


class BootReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action === Intent.ACTION_BOOT_COMPLETED ||
            intent.action === Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            intent.action === Intent.ACTION_REBOOT
        ) {
            val i = Intent(context, PersistentNotificationService::class.java)
            context.startService(i)
            BirthdayNotificationWorker.enqueueSelf(context, notifyHasStarted = true, restart = true)
        }

    }

}
