package com.example.bikecomputer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.preference.PreferenceManager
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

fun getCircumference(context: Context): ByteArray{
    val shared = PreferenceManager.getDefaultSharedPreferences(context)
    val circString = shared.getString("circumference", "0")
    var circumference = 0F
    if (circString != null){
        circumference = Integer.parseInt(circString).toFloat()*0.001F
    }

    return (circumference).toByteArray()
}

fun getCurrentDate():String{
    val date = LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("d/M/y"))

}

fun elapsedTime(chronometerBase: Long): String{
    val elapsedMillis = SystemClock.elapsedRealtime() - chronometerBase
    val seconds = ((elapsedMillis / 1000).toInt() % 60)
    val minutes = (elapsedMillis / (1000 * 60) % 60)
    val hours = (elapsedMillis / (1000 * 60 * 60) % 24)
    return if(hours != 0L){
        "$hours:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
    else{
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}