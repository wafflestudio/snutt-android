package com.wafflestudio.snutt2.components.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay

enum class TimerValue {
    Initial, Running, End, Paused,
}

class TimerState(
    initialValue: TimerValue,
    private var startTime: Long,
    private val duration: Long,
) {
    val isInitial: Boolean
        get() = currentValue == TimerValue.Initial

    val isRunning: Boolean
        get() = currentValue == TimerValue.Running

    val isPaused: Boolean
        get() = currentValue == TimerValue.Paused

    val isEnded: Boolean
        get() = currentValue == TimerValue.End

    val timeLeftInSecond: Int
        get() = if (isPaused) {
            ((startTime + pausedTime + duration - pauseStartTime) / 1000).toInt()
        } else {
            ((startTime + pausedTime + duration - System.currentTimeMillis()) / 1000).toInt()
        }

    var pausedTime: Long = 0
    var pauseStartTime: Long = 0

    var currentValue: TimerValue by mutableStateOf(initialValue)
        private set

    fun start() {
        currentValue = TimerValue.Running
        startTime = System.currentTimeMillis()
    }

    fun pause() {
        if (!isRunning) return
        currentValue = TimerValue.Paused
        pauseStartTime = System.currentTimeMillis()
    }

    fun resume() {
        if (!isPaused) return
        currentValue = TimerValue.Running
        pausedTime += System.currentTimeMillis() - pauseStartTime
    }

    fun end() {
        currentValue = TimerValue.End
    }

    fun reset() {
        currentValue = TimerValue.Initial
        pausedTime = 0
        pauseStartTime = 0
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
    var timeText by remember { mutableStateOf("${state.timeLeftInSecond / 60}:${"%02d".format(state.timeLeftInSecond % 60)}") }

    LaunchedEffect(state.currentValue) {
        if (state.isEnded) {
            timeText = endMessage
        } else {
            while (true) {
                if (state.timeLeftInSecond <= 0) {
                    state.end()
                    break
                } else {
                    timeText = "${state.timeLeftInSecond / 60}:${"%02d".format(state.timeLeftInSecond % 60)}"
                }
                delay(1000L)
            }
        }
    }
    content(timeText)
}

@Composable
fun rememberTimerState(
    initialValue: TimerValue,
    durationInSecond: Int,
): TimerState {
    return rememberSaveable(saver = TimerState.Saver()) {
        TimerState(initialValue, System.currentTimeMillis(), durationInSecond * 1000L)
    }
}
