package com.example.moviescatalog.presentation.view.player.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Timer(
    private var delayMillis: Long,
    private val repeats: Boolean = true,
    private val action: () -> Unit
) {

    private var job: Job? = null

    private var isRunning = false

    fun start(initialTick: Boolean = false, newDelayMs: Long? = null) {
        stop()
        delayMillis = newDelayMs ?: delayMillis
        isRunning = true

        if (initialTick) {
            action()
        }

        job = CoroutineScope(Dispatchers.Main).launch {
            delay(delayMillis)
            if (!repeats) {
                stop()
            }
            action()

            while (repeats) {
                delay(delayMillis)
                action()
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        job = null
    }

    fun reset() {
        if (!isRunning)
            return

        stop()
        start()
    }
}