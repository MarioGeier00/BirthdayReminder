package com.mgprogramms.birthdayreminder.ui.history

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.mgprogramms.birthdayreminder.NotificationLogger
import com.mgprogramms.birthdayreminder.R
import com.mgprogramms.birthdayreminder.databinding.ActivityNotificationHistoryBinding

class NotificationHistory : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationHistoryBinding

    private lateinit var notifications: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        val notificationSet = NotificationLogger.getNotifications(applicationContext).toMutableList()
        notificationSet.sort();
        notifications =
            ArrayAdapter(
                applicationContext,
                android.R.layout.simple_list_item_1,
                notificationSet
            )
        binding.test.notificationList.adapter = notifications

        ViewCompat.setNestedScrollingEnabled(binding.test.notificationList, true)

        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener {
            notifications.clear()
            NotificationLogger.clear(applicationContext)
        }
    }
}
