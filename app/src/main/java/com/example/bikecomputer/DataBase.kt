package com.example.bikecomputer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class TripDataBase(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "Trips"
        private const val DATABASE_VERSION = 1
        private const val TRIP_TABLE = "TRIP_TABLE"
        private const val COLUMN_ID = "ID"
        private const val COLUMN_DISTANCE = "DISTANCE"
        private const val COLUMN_AVGSPEED = "AVGSPEED"
        private const val COLUMN_DATE = "DATE"
        private const val COLUMN_TIME = "TIME"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableStatement =
            "CREATE TABLE $TRIP_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_DISTANCE TEXT, $COLUMN_AVGSPEED TEXT, $COLUMN_DATE TEXT, $COLUMN_TIME TEXT)"

        db?.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addTrip(tripModel: TripModel): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val cv =  ContentValues()

        cv.put(COLUMN_DATE, tripModel.getDate())
        cv.put(COLUMN_DISTANCE, tripModel.getDistance())
        cv.put(COLUMN_AVGSPEED, tripModel.getAvgSpeed())
        cv.put(COLUMN_TIME, tripModel.getTime())

        val success = db.insert(TRIP_TABLE, null, cv)
        db.close()

        return success.compareTo(-1) != 0
    }

    fun getTrips(): MutableList<TripModel> {
        val returnList:ArrayList<TripModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TRIP_TABLE"
        val db = this.readableDatabase
        val cursor: Cursor?


        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if (cursor.moveToFirst()){
            do {
                val tripID = cursor.getInt(0)
                val tripDistance = cursor.getString(1)
                val tripAvgSpeed = cursor.getString(2)
                val tripDate = cursor.getString(3)
                val tripTime = cursor.getString(4)

                val newTrip = TripModel(tripID, tripDistance, tripDate,tripAvgSpeed, tripTime)
                returnList.add(newTrip)
            } while(cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return returnList
    }

    fun deleteTrip(tripModel: TripModel): Boolean {
        val db = this.writableDatabase
        val queryString = "DElETE FROM $TRIP_TABLE WHERE $COLUMN_ID = ${tripModel.getId()}"

        val cursor = db.rawQuery(queryString, null)

        val notEmpty = cursor.moveToFirst()
        cursor.close()
        return notEmpty

    }

}