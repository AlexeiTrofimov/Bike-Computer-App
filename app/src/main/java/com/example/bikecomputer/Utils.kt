package com.example.bikecomputer

import android.os.Handler
import android.os.Looper
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Run {
    companion object {
        fun after(delay: Long, process: () -> Unit) {
            Handler(Looper.getMainLooper()).postDelayed({
                process()
            }, delay)
        }
    }
}

fun ByteArray.toFloat(): Float =
    ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).float

fun Float.toByteArray(): ByteArray =
    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(this).array()

fun ByteArray.toHexString(): String =
    joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }