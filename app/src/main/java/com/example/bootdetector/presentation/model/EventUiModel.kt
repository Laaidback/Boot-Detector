package com.example.bootdetector.presentation.model

data class EventUiModel(
    val text: String,
)

data class InputFieldModel(
    val value: String,
    val label: String,
    val onValueChanged: (String) -> Unit,
)

sealed class BootScreenUiState {
    data object Loading : BootScreenUiState()
    data class Loaded(
        val events: List<EventUiModel>,
        val totalDismissalsInput: InputFieldModel,
        val intervalInput: InputFieldModel,
    ) : BootScreenUiState()
}
