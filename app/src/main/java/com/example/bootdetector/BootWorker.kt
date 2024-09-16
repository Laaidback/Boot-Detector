package com.example.bootdetector

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.bootdetector.domain.BootRepository
import com.example.bootdetector.domain.model.BootEvent
import com.example.bootdetector.receiver.DismissReceiver
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

fun scheduleBootWorker(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<BootWorker>(15, TimeUnit.MINUTES).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "BootWorker", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, workRequest
    )
}


@HiltWorker
class BootWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bootRepository: BootRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        createNotification()
        return Result.success()
    }

    private suspend fun createNotification() {
        val dismissCount = bootRepository.getDismissalCount()
        val channelId = "boot_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Boot Event Channel"
            val descriptionText = "Channel for Boot Event Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val dismissIntent = Intent(applicationContext, DismissReceiver::class.java).apply {
            putExtra("DISMISS_COUNT", dismissCount)
        }

        val dismissPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val title = "Boot Event Detected"

        val bootEvents = bootRepository.getBootEventData()
        val message = createMessage(bootEvents)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title)
            .setDeleteIntent(dismissPendingIntent).setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            println("asd PERMISSION_GRANTED not")
            return
        }
        NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
    }

    private fun createMessage(bootEvents: List<BootEvent>): String {
        return when {
            bootEvents.isEmpty() -> {
                "No boots detected"
            }

            bootEvents.size == 1 -> {
                val singleEvent = bootEvents.first()
                formatBootEvent(singleEvent)
            }

            else -> {
                val lastEvent = bootEvents.last()
                val secondLastEvent = bootEvents[bootEvents.size - 2]
                val timeDelta = calculateTimeDelta(lastEvent.timestamp, secondLastEvent.timestamp)
                "Last boots time delta = ${formatTimeDelta(timeDelta)}"
            }
        }
    }

    private fun formatBootEvent(bootEvent: BootEvent): String {
        return "The boot was detected = ${bootEvent.timestamp.format("dd/MM/yyyy HH:mm:ss")}"
    }

    private fun calculateTimeDelta(
        lastTimestamp: LocalDateTime, secondLastTimestamp: LocalDateTime
    ): Duration {
        val lastInstant = lastTimestamp.toInstant(TimeZone.currentSystemDefault())
        val secondLastInstant = secondLastTimestamp.toInstant(TimeZone.currentSystemDefault())
        return secondLastInstant - lastInstant
    }

    private fun formatTimeDelta(timeDelta: Duration): String {
        val days = timeDelta.inWholeDays
        val hours = timeDelta.inWholeHours % 24
        val minutes = timeDelta.inWholeMinutes % 60
        val seconds = timeDelta.inWholeSeconds % 60

        return buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (seconds > 0) append("${seconds}s")
        }.trim()
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun LocalDateTime.format(pattern: String): String {
        val format = LocalDateTime.Format { byUnicodePattern(pattern) }
        return this.format(format)
    }
}
