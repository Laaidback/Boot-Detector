package com.example.bootdetector.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationManagerCompat
import com.example.bootdetector.scheduleBootWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Intent>

    private val viewModel: BootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.uiState.collectAsState()
            BootScreen(state)
        }
        checkPermissionsAndLaunchWorker()
    }

    private fun checkPermissionsAndLaunchWorker() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (areNotificationsEnabled(this)) {
                    launchWorkManager()
                } else {
                    Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        if (!areNotificationsEnabled(this)) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            requestPermissionLauncher.launch(intent)
        } else {
            launchWorkManager()
        }
    }

    private fun launchWorkManager() {
        scheduleBootWorker(this)
    }

    private fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}

