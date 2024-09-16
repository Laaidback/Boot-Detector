package com.example.bootdetector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bootdetector.BootWorker
import com.example.bootdetector.domain.BootRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var bootRepository: BootRepository

    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            bootRepository.incrementDismissalCount()
            val dismissCount = bootRepository.getDismissalCount()
            rescheduleNotificationAfterDismissal(context, dismissCount)
        }
    }

    private fun rescheduleNotificationAfterDismissal(context: Context, dismissCount: Int) {
        val delay = if (dismissCount > 0) {
            dismissCount * 20L
        } else 15
        val notificationWorkRequest = OneTimeWorkRequestBuilder<BootWorker>()
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWorkRequest)
    }
}
