package com.example.bikecomputer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
        recyclerView.adapter = TripsAdapter (dataBase.getTrips()) { tripModel ->
            dataBase.deleteTrip(tripModel)
        }
    }
}

class TripsAdapter(trips: MutableList<TripModel>, onButtonClicked: (TripModel) -> Unit): RecyclerView.Adapter<CustomViewHolder>(){
    private val allTrips = trips
    override fun getItemCount(): Int {
        return allTrips.size
    }
    private val onButtonClick = onButtonClicked

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.trip_data_row, parent, false)
        return CustomViewHolder(cellForRow){
            onButtonClick(allTrips[it])
            deleteItem(allTrips[it])
        }
    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val distanceText = allTrips[position].getDistance() + " km"
        holder.distanceText.text = distanceText
        holder.dateText.text = allTrips[position].getDate()
        holder.timeText.text = allTrips[position].getTime()
        holder.id = allTrips[position].getId()
    }

    private fun deleteItem(model: TripModel){
        val position = allTrips.indexOf(model)
        allTrips.removeAt(position)
        this.notifyItemRemoved(position)
    }
}

class CustomViewHolder(v: View, onButtonClicked: (Int) -> Unit): RecyclerView.ViewHolder(v){
    init {
        val deleteButton: ImageButton = v.findViewById(R.id.delete_trip_btn)
        deleteButton.setOnClickListener{
            onButtonClicked(adapterPosition)
        }
    }
    var id: Int = 0
    val dateText: TextView = v.findViewById(R.id.trip_date_text)
    val timeText: TextView = v.findViewById(R.id.trip_time_view)
    val distanceText: TextView = v.findViewById(R.id.trip_distance_text)
}