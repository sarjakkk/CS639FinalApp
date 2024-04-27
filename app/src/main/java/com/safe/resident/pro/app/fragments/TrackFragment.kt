package com.safe.resident.pro.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.safe.resident.pro.app.R
import com.safe.resident.pro.app.databinding.FragmentTrackBinding

class TrackFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentTrackBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false)
        val view = binding.root


        return view
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }
}