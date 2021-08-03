package com.example.bikecomputer

class TripModel(private var id: Int, private var distance: Int, private var date: String,
                private var time: String
)  {

    fun getId(): Int{
        return id
    }

    fun setId(id: Int){
        this.id = id
    }

    fun getDistance(): Int{
        return distance
    }

    fun setDistance(distance: Int){
        this.distance = distance
    }

    fun getDate(): String{
        return date
    }

    fun setDate(date: String){
        this.date = date
    }

    fun getTime(): String{
        return time
    }

    fun setTime(time: String){
        this.time = time
    }
}

