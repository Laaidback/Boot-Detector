package com.example.bootdetector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bootdetector.domain.SaveBootEventUseCase
import com.example.bootdetector.scheduleBootWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var saveBootEventUseCase: SaveBootEventUseCase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            GlobalScope.launch {
                scheduleBootWorker(context)
                saveBootEventUseCase.execute()
            }
        }
    }
}