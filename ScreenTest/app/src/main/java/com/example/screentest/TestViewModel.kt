package com.example.screentest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {
    private val _testState = MutableStateFlow<TestState>(TestState.Default)
    val testState: StateFlow<TestState> = _testState

    private val _timeRemaining = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining

    fun startTest(timeOut: Long) {
        _testState.value = TestState.Testing

        viewModelScope.launch {
            for (i in timeOut downTo 1) {
                _timeRemaining.value = i.toInt()
                delay(1000)
            }
            if (_testState.value == TestState.Testing) {
                _testState.value = TestState.Failure
            }
        }
    }

    fun completeTest() {
        _testState.value = TestState.Success
    }
}