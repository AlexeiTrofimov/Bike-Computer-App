package com.example.bikecomputer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.*
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

        val currentItem = allTrips[position]
        val distanceText = currentItem.getDistance() + " km"
        holder.distanceText.text = distanceText
        holder.dateText.text = currentItem.getDate()
        holder.timeText.text = currentItem.getTime()
        holder.id = currentItem.getId()
        holder.avgSpeedText.text = currentItem.getAvgSpeed()

        val isVisible: Boolean = currentItem.getVisibility()

        holder.expandedLayout.visibility = if (isVisible) View.VISIBLE else View.GONE

        holder.expandImg.setOnClickListener {
            currentItem.setVisibility(!currentItem.getVisibility())
            notifyItemChanged(position)
        }
    }

    private fun deleteItem(model: TripModel){
        val position = allTrips.indexOf(model)
        allTrips.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyItemRangeChanged(position, allTrips.size)
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
    val avgSpeedText: TextView = v.findViewById(R.id.avg_speed_text)
    val expandImg: ImageView = v.findViewById(R.id.expand_img)
    val expandedLayout: LinearLayout = v.findViewById(R.id.expandedLayout)

}