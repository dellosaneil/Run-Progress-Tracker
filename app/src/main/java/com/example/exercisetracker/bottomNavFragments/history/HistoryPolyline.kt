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
import com.example.exercisetracker.databinding.FragmentHistoryPolylineBinding
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.utility.UtilityFunctions.dateFormatter
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

    private var _binding: FragmentHistoryPolylineBinding? = null
    private val binding get() = _binding!!


    private val callback = OnMapReadyCallback { googleMap ->
        polyLines = args?.polylines?.route
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyLines?.get(0), 18f))
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap.addPolyline(
            PolylineOptions()
                .addAll(polyLines)
                .color(Color.RED)
                .width(10f)
        )
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
    ): View {
        _binding = FragmentHistoryPolylineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.historyPolyline_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        labelMaterialToolbar()
    }

    private fun labelMaterialToolbar() {
        binding.historyPolylineToolbar.title = "${args?.polylines?.modeOfExercise}\t\t(${dateFormatter(args?.polylines?.startTime!!)})"
        binding.historyPolylineToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

}