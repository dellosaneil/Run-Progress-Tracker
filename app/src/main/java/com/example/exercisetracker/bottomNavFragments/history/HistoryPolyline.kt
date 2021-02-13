package com.example.exercisetracker.bottomNavFragments.history

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class HistoryPolyline : Fragment() {

    private val args: HistoryPolylineArgs? by navArgs()
    private var polyLines : List<LatLng>? = null

    private val TAG = "HistoryPolyline"

    private val callback = OnMapReadyCallback { googleMap ->
        polyLines = args?.polylines?.route
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyLines?.get(0), 18f))
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(polyLines)
                .color(Color.RED)
                .width(10f)
        )
        Log.i(TAG, "Average Speed:${args?.polylines?.averageSpeed} ")
        placePolylineIndicators(googleMap)
    }

    private fun placePolylineIndicators(googleMap: GoogleMap?) {
        val positions = arrayOf(polyLines!![0], polyLines!!.last())
        val polylineColors = arrayOf(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )
        val polylineTitle = arrayOf(
            getString(R.string.workoutHistoryPolyline_start),
            getString(R.string.workoutHistoryPolyline_end)
        )
        repeat(2) {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(positions[it])
                    .icon(polylineColors[it])
                    .title(polylineTitle[it])
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_polyline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.historyPolyline_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}