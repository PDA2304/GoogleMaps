package com.example.googlemaps

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*
import kotlin.collections.HashSet

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var test: Geocoder
    private var latitude: Double = -1.0
    private var longitude: Double = -1.0
    private lateinit var sp: SharedPreferences
    private var latList: MutableList<String> = mutableListOf()
    private var longList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sp = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val sizeLat = sp.getInt("latlist_size", 0)
        val sizeLong = sp.getInt("longlist_size", 0)
        for (i in 0 until sizeLat) {
            latList.add(sp.getString("latlist_" + i, null)!!)
            longList.add(sp.getString("longlist_" + i, null)!!)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
            for (i in 0 until latList.size) {
                val point = LatLng(latList[i].toDouble(), longList[i].toDouble())
                mMap.addMarker(
                    MarkerOptions().position(point)
                )
            }
    }

    fun Add(view: View) {
        test = Geocoder(this)
        val thread: Thread = object : Thread() {
            override fun run() {
                var t = test.getFromLocationName(City.text.toString(), 1)
                if (!t.isEmpty()) {
                    latitude = t[0].latitude
                    longitude = t[0].longitude
                    latList.add(latitude.toString())
                    longList.add(longitude.toString())
                    Log.i("tag", t[0].latitude.toString() + " " + t[0].longitude.toString())
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@MapsActivity,
                            "Данные были введены не корректно",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        thread.start()
        Thread.sleep(1000)

        val editor = sp.edit()
        editor.putInt("latlist_size", latList.size)
        editor.putInt("longlist_size", longList.size)
        for (i in 0 until latList.size) {
            editor.putString(
                "latlist_" + i,
                latList[i]
            )
            editor.putString("longlist_" + i, longList[i])
        }

        editor.apply()
        val point = LatLng(
            latitude,
            longitude
        )
        mMap.addMarker(MarkerOptions().position(point).title(City.text.toString()))
    }
}
