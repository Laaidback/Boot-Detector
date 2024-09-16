package com.example.bootdetector.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bootdetector.presentation.model.BootScreenUiState
import com.example.bootdetector.presentation.model.EventUiModel
import com.example.bootdetector.presentation.model.InputFieldModel

@Composable
fun BootScreen(uiState: BootScreenUiState) {
    when (uiState) {
        is BootScreenUiState.Loading -> LoadingScreen()
        is BootScreenUiState.Loaded -> LoadedScreen(uiState)
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadedScreen(state: BootScreenUiState.Loaded) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Boot Events",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        state.events.forEach { event ->
            EventItem(event)
        }
        Spacer(modifier = Modifier.height(16.dp))
        InputField(model = state.totalDismissalsInput)
        Spacer(modifier = Modifier.height(16.dp))
        InputField(model = state.intervalInput)
    }
}

@Composable
fun EventItem(event: EventUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = event.text,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun InputField(
    model: InputFieldModel
) {
    Column {
        Text(
            text = model.label,
            style = MaterialTheme.typography.titleSmall,
        )
        BasicTextField(
            value = model.value,
            onValueChange = { model.onValueChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
    }
}
