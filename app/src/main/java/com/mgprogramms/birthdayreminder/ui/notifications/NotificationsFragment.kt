package com.mgprogramms.birthdayreminder.ui.notifications

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mgprogramms.birthdayreminder.BirthdayNotificationWorker
import com.mgprogramms.birthdayreminder.R
import com.mgprogramms.birthdayreminder.TestWorker
import com.mgprogramms.birthdayreminder.databinding.FragmentNotificationsBinding
import com.mgprogramms.birthdayreminder.ui.history.NotificationHistory
import java.time.Duration


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val durationUntilNextNotification = BirthdayNotificationWorker.getDurationUntilNextNotification()
        binding.nextNotification.text = String.format(
            resources.getString(R.string.next_notification),
            durationUntilNextNotification.toHours(),
            durationUntilNextNotification.toMinutes() % (durationUntilNextNotification.toHours() * 60)
        )

        // only load the notification state once in the beginning to
        // make sure the setOnCheckedChangeListener is not called
        binding.notificationSwitch.isChecked = notificationsViewModel.notifications.value == true

        binding.notificationSwitch.setOnCheckedChangeListener { _, _ ->
            notificationsViewModel.updateNotificationState(binding.notificationSwitch.isChecked)
        }


        binding.removableNotifications.isChecked = notificationsViewModel.removeNotificationsActivated()

        binding.removableNotifications.setOnCheckedChangeListener { _, _ ->
            notificationsViewModel.updateRemoveNotifications(binding.removableNotifications.isChecked)
        }


        binding.serviceAppStart.isChecked = BirthdayNotificationWorker.enqueueAtAppStartup(requireContext())

        binding.serviceAppStart.setOnCheckedChangeListener { _, _ ->
            BirthdayNotificationWorker.setEnqueueAtAppStartup(requireContext(), binding.serviceAppStart.isChecked)
        }


        binding.testWorker.setOnClickListener {
            val workRequest = OneTimeWorkRequestBuilder<TestWorker>().apply {
                setInitialDelay(Duration.ofSeconds(binding.delayTestWorker.text.toString().toLong()))
            }.build()
            WorkManager.getInstance(requireContext()).enqueue(workRequest)
        }
        binding.openNotificationHistory.setOnClickListener {
            requireContext().startActivity(Intent(context, NotificationHistory::class.java))
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
