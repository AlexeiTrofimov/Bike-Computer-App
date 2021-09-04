package com.example.bikecomputer

class TripModel(private var id: Int, private var distance: String, private var date: String,
                private var avgSpeed: String, private var time: String, private var visibility: Boolean = false
)  {

    fun getId(): Int{
        return id
    }

    fun setId(id: Int){
        this.id = id
    }

    fun getDistance(): String{
        return distance
    }

    fun setDistance(distance: String){
        this.distance = distance
    }

    fun getDate(): String{
        return date
    }

    fun setDate(date: String){
        this.date = date
    }

    fun getAvgSpeed(): String{
        return avgSpeed
    }

    fun getTime(): String{
        return time
    }

    fun setTime(time: String){
        this.time = time
    }

    fun getVisibility(): Boolean{
        return visibility
    }

    fun setVisibility(bool: Boolean){
        this.visibility = bool
    }
}

