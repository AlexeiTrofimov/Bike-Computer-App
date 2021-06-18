package com.example.bikecomputer

import android.os.Handler
import android.os.Looper

class Run {
    companion object {
        fun after(delay: Long, process: () -> Unit) {
            Handler(Looper.getMainLooper()).postDelayed({
                process()
            }, delay)
        }
    }
}