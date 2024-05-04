package com.safe.resident.pro.app.fragments

import android.Manifest
<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
<<<<<<< HEAD
import android.os.Bundle
=======
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
<<<<<<< HEAD
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
=======
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
<<<<<<< HEAD
import com.google.firebase.Firebase
=======
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
<<<<<<< HEAD
import com.google.firebase.database.database
import com.safe.resident.pro.app.AccountActivity
import com.safe.resident.pro.app.MainActivity
=======
import com.safe.resident.pro.app.AccountActivity
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
import com.safe.resident.pro.app.R
import com.safe.resident.pro.app.data.Incident
import com.safe.resident.pro.app.databinding.FragmentTrackBinding

class TrackFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentTrackBinding
    private lateinit var googleMap: GoogleMap
    private var currentLocation: Location? = null
    private val radiusMeters = 1207.01 // 1.5 Miles area
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var database: DatabaseReference
    private val incidentsList = mutableListOf<Incident>()
<<<<<<< HEAD
=======
    private lateinit var locationManager: LocationManager

>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
<<<<<<< HEAD
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false)
=======
        binding = FragmentTrackBinding.inflate(inflater, container, false)
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
        val view = binding.root
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        database = FirebaseDatabase.getInstance().reference
<<<<<<< HEAD
        fetchIncidents()
        binding.ivUser.setOnClickListener {
            startActivity(Intent( requireActivity(), AccountActivity::class.java))
        }
        return view
    }
=======
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fetchIncidents()
        binding.ivUser.setOnClickListener {
            startActivity(Intent(requireActivity(), AccountActivity::class.java))
        }
        return view
    }

>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
    private fun fetchIncidents() {
        database.child("incidents").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                incidentsList.clear()
                for (incidentSnapshot in snapshot.children) {
                    val incident = incidentSnapshot.getValue(Incident::class.java)
                    incident?.let {
                        incidentsList.add(it)
                    }
                }
                displayIncidentsOnMap()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
<<<<<<< HEAD
=======

>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
    private fun displayIncidentsOnMap() {
        for (incident in incidentsList) {
            incident.latLong?.split(",")?.let { latLong ->
                if (latLong.size == 2) {
                    val latitude = latLong[0].toDouble()
                    val longitude = latLong[1].toDouble()
                    val location = LatLng(latitude, longitude)
                    addCustomMarker(incident.incidentName, location)
                }
            }
        }
    }

    private fun addCustomMarker(incidentName: String?, location: LatLng) {
        if (isAdded) {
            val markerOptions = MarkerOptions().position(location)
            val drawableId = when (incidentName) {
                "Police Activity" -> R.drawable.police
                "Robbery" -> R.drawable.thief
                "Accident" -> R.drawable.accident
                "Fire" -> R.drawable.fire
                "Fighting" -> R.drawable.fight
                else -> R.drawable.mark_alert // Default drawable if incidentName doesn't match
            }
            val markerDrawable = ContextCompat.getDrawable(requireContext(), drawableId)
            markerDrawable?.let {
<<<<<<< HEAD
                val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, drawableId))
=======
                val bitmapDescriptor =
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, drawableId))
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
                markerOptions.icon(bitmapDescriptor)
            }
            val marker = googleMap.addMarker(markerOptions)
            markerList.add(marker!!) // Keep track of added markers
        }
    }
<<<<<<< HEAD
=======

>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
    private val markerList = mutableListOf<Marker>()

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.apply {
                isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
                setOnMyLocationChangeListener { location ->
                    currentLocation = location
                    updateMapLocation()
                }
            }
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showExplanationDialog()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the location permission to provide location-based services. Please allow access to continue.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                requestLocationPermission()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        enableMyLocation()
        displayIncidentsOnMap()
<<<<<<< HEAD
=======
        checkGpsStatus()
    }

    private fun checkGpsStatus() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsAlertDialog()
        }
    }

    private fun showGpsAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("GPS Required")
            .setMessage("Please enable GPS to track your current location.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
    }

    private fun updateMapLocation() {
        currentLocation?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)
            googleMap.clear()
            // Add custom markers again after clearing the map
            displayIncidentsOnMap()

            googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

            val circleOptions = CircleOptions()
                .center(currentLatLng)
                .radius(radiusMeters.toDouble())
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(70, 0, 0, 255))
            googleMap.addCircle(circleOptions)
        }
    }
<<<<<<< HEAD
=======

>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                showDenialDialog()
            }
        }
    }

    private fun showDenialDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Location permission is essential for this app to function. Please consider granting it in app settings.")
            .setPositiveButton("Settings") { dialog, _ ->
                // Open app settings here if desired
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 242435742d92f0d87ca8df0d86b172a5a71ffa76
