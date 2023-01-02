package com.wafflestudio.snutt2.components.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class TimerValue {
    Initial, Running, End, Paused,
}

class TimerState(
    initialValue: TimerValue,
    val endTimeInSecond: Int,
) {
    val isInitial: Boolean
        get() = currentValue == TimerValue.Initial

    val isRunning: Boolean
        get() = currentValue == TimerValue.Running

    val isEnded: Boolean
        get() = currentValue == TimerValue.End

    var time by mutableStateOf(endTimeInSecond)

    var currentValue: TimerValue by mutableStateOf(initialValue)
        private set

    fun start() {
        currentValue = TimerValue.Running
    }

    fun pause() {
        currentValue = TimerValue.Paused
    }

    fun resume() {
        currentValue = TimerValue.Running
    }

    fun end() {
        currentValue = TimerValue.End
    }

    fun reset() {
        currentValue = TimerValue.Initial
        time = endTimeInSecond
    }

    companion object {
        fun Saver() = Saver<TimerState, TimerValue>(save = { it.currentValue }, restore = { null })
    }
}

@Composable
fun Timer(
    state: TimerState,
    endMessage: String,
    content: @Composable (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var timeText = if (state.isEnded) endMessage
    else "${state.time / 60}:${"%02d".format(state.time % 60)}"

    LaunchedEffect(state.currentValue) {
        if (state.isInitial) state.time = state.endTimeInSecond
        else if (state.isRunning) {
            scope.launch {
                while (state.time > 0 && state.isRunning) {
                    delay(1000L)
                    state.time--
                }
                if (state.time == 0) state.end()
            }
        } else if (state.isEnded) {
            timeText = endMessage
        }
    }
    content(timeText)
}

@Composable
fun rememberTimerState(
    initialValue: TimerValue,
    endTime: Int,
): TimerState {
    return rememberSaveable(saver = TimerState.Saver()) {
        TimerState(initialValue, endTime)
    }
}
