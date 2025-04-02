package com.example.screentest

sealed class TestState {
    object Default : TestState()
    object Testing : TestState()
    object Success : TestState()
    object Failure : TestState()
}