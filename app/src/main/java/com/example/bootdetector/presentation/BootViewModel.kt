package com.example.bootdetector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bootdetector.domain.BootRepository
import com.example.bootdetector.domain.model.BootEvent
import com.example.bootdetector.domain.model.Config
import com.example.bootdetector.presentation.model.BootScreenUiState
import com.example.bootdetector.presentation.model.EventUiModel
import com.example.bootdetector.presentation.model.InputFieldModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import javax.inject.Inject

@HiltViewModel
class BootViewModel @Inject constructor(
    private val bootRepository: BootRepository
) : ViewModel() {

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDate.Format {
        byUnicodePattern(formatPattern)
    }

    private val _uiState = MutableStateFlow<BootScreenUiState>(BootScreenUiState.Loading)
    val uiState: StateFlow<BootScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val bootEvents = bootRepository.getBootEventData()
            bootRepository.getConfig().collect { config ->
                _uiState.value = BootScreenUiState.Loaded(
                    events = eventUiModels(bootEvents),
                    totalDismissalsInput = total(config),
                    intervalInput = intervalInput(config),
                )
            }
        }
    }

    private fun eventUiModels(bootEvents: List<BootEvent>) = when {
        bootEvents.isEmpty() -> listOf(
            EventUiModel(
                text = "No boots detected"
            )
        )

        else -> bootEvents
            .groupBy { it.timestamp.date }
            .map { (date, events) ->
                val formattedDate = date.format(dateTimeFormat)
                val count = events.size
                EventUiModel(text = "$formattedDate - $count")
            }
    }

    private fun total(config: Config) =
        InputFieldModel(
            value = config.maxDismissals.toString(),
            label = "Total Dismissals Allowed",
            onValueChanged = {
                // todo: fix input handling
                it.toIntOrNull()?.let { numberInput ->
                    viewModelScope.launch {
                        bootRepository.saveConfig(
                            config = config.copy(
                                maxDismissals = numberInput
                            )
                        )
                    }
                }
            }
        )

    private fun intervalInput(config: Config) =
        InputFieldModel(
            value = config.intervalMultiplier.toString(),
            label = "Interval Between Dismissals (minutes)",
            onValueChanged = {
                it.toIntOrNull()?.let { numberInput ->
                    viewModelScope.launch {
                        bootRepository.saveConfig(
                            config = config.copy(
                                intervalMultiplier = numberInput
                            )
                        )
                    }
                }
            }
        )
}

private const val formatPattern = "dd-MM-yyyy"
