package com.example.exercisetracker.bottomNavFragments.history

import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOngoingArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class HistoryPolyline : Fragment() {

    private val args: HistoryPolylineArgs? by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->
        val polyLines = args?.polylines?.route
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyLines?.get(0), 18f))
        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(polyLines)
                .color(Color.RED)
                .width(10f)
        )
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.historyPolyline_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}