package com.example.bikecomputer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_trips)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val dataBase = TripDataBase(applicationContext)
        recyclerView.adapter = TripsAdapter(dataBase.getTrips())

    }
}

class TripsAdapter(trips: List<TripModel>): RecyclerView.Adapter<CustomViewHolder>(){
    private val allTrips = trips
    override fun getItemCount(): Int {
        return allTrips.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.trip_data_row, parent, false)
        return CustomViewHolder(cellForRow)
    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.dateText.text = allTrips[position].getDate()
        holder.distanceText.text =  "${allTrips[position].getDistance()} km"
        holder.timeText.text = allTrips[position].getTime()
    }
}

class CustomViewHolder(v: View): RecyclerView.ViewHolder(v){
    val dateText: TextView = v.findViewById(R.id.trip_date_text)
    val timeText: TextView = v.findViewById(R.id.trip_time_view)
    val distanceText: TextView = v.findViewById(R.id.trip_distance_text)

}