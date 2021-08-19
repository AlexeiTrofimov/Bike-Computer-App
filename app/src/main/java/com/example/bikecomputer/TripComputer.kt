package com.example.bikecomputer

import android.content.Context
import android.util.Log
import kotlin.math.PI

class TripComputer (private var revolutions: Int = 0)
{
    private val speeds: MutableList<Float> = ArrayList()
    fun getDistance(context: Context): Double {
        val diameter = getDiameter(context).toFloat()
        return PI*revolutions*diameter*0.001F // returns in km
    }

    fun addRevolution(){
        revolutions += 1
    }

    fun addSpeed(toAdd: Float){
        if (revolutions < 100){
            speeds.add(toAdd)
        }
        else{
            speeds[revolutions%100] = toAdd
        }
    }

    fun getAverageSpeed(): Float{
        var averageSpeed = 0F
        return if(speeds.size != 0){
            for (speed in speeds){
                averageSpeed += speed
            }
            averageSpeed/speeds.size
        }else{
            0F
        }

    }
}