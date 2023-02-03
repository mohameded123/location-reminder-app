package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.webkit.GeolocationPermissions
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.firebase.ui.auth.util.GoogleApiUtils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.internal.GoogleApiManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragmentDirections
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.android.synthetic.main.fragment_select_location.*
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.ext.getFullName
import java.lang.Exception
import java.util.Objects
import java.util.concurrent.Executor

class SelectLocationFragment : BaseFragment()   , OnMapReadyCallback{

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private  lateinit var map: GoogleMap

lateinit  var  inflater: LayoutInflater
    var container: ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        this.container = container
        this.inflater = inflater




            binding =
                DataBindingUtil.inflate(inflater,
                    R.layout.fragment_select_location,
                    container,
                    false)






            binding.viewModel = _viewModel
            binding.lifecycleOwner = this

            setHasOptionsMenu(true)
            setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location
            val mf = this.childFragmentManager.findFragmentById(R.id.map11) as SupportMapFragment

            mf.getMapAsync(this@SelectLocationFragment)





        return binding.root
    }
    private fun isPermissionGranted() : Boolean {


            return  requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED


    }

    @SuppressLint("MissingPermission")
     fun enableMyLocation() {
        if (isPermissionGranted()) {

            location_access()

        }
        else {

              ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    requestcode
                )
            }
    }
    private   val  requestcode = 100
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray) {
//
//        if (requestCode == rcode) {
//            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                enableMyLocation()
//            }
//        }
//    }

var  rcode = 555
    @SuppressLint("MissingPermission")
    private   fun  location_access()
    {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener {
                exception ->
            var k = true
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this@SelectLocationFragment.requireActivity(),
                        2)
                } catch (sendEx: IntentSender.SendIntentException) {
                   _viewModel.showSnackBar.value = "please enable location "
                    k = false

                }
                finally {
                    if(k)
                    {
                        map.setMyLocationEnabled(true)
                        locate().addOnCompleteListener{


                            it.result?.let {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom( LatLng(it.latitude ,it.longitude) ,15f))

                            } ?:   Toast.makeText(requireContext() , "Failed to enable location" ,Toast.LENGTH_SHORT).show()


                        }

                    }

                }

            }



        }
        locationSettingsResponseTask.addOnSuccessListener {
            map.setMyLocationEnabled(true)
            locate().addOnCompleteListener{


                it.result?.let {

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom( LatLng(it.latitude ,it.longitude) ,15f))

                } ?:   Toast.makeText(requireContext() , "Failed to enable location" ,Toast.LENGTH_SHORT).show()


            }


        }
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        map.setOnMapLongClickListener {
            map.addMarker(MarkerOptions().position(it))
           _viewModel.selectedPOI.value =  PointOfInterest(it ,_viewModel.reminderTitle.value , _viewModel.reminderTitle.value )
        _viewModel.longitude.value = it.longitude
            _viewModel.latitude.value = it.latitude

          _viewModel.reminderSelectedLocationStr.value =  _viewModel.selectedPOI.value!!.latLng.toString()

        }
        map.setOnPoiClickListener {
            map.addMarker(MarkerOptions().position(it.latLng))
            _viewModel.selectedPOI.value = it
            _viewModel.longitude.value = it.latLng.longitude
            _viewModel.latitude.value = it.latLng.latitude
            _viewModel.reminderSelectedLocationStr.value =  _viewModel.selectedPOI.value!!.latLng.toString()
        }

    }
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMapReady(p0: GoogleMap?) {
        try {
            map = p0!!
        }catch (e :java.lang.Exception)
        {
            Toast.makeText(requireContext() ,"map loading failed" ,Toast.LENGTH_LONG).show()
        }
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext() ,R.raw.mapstyle))


          enableMyLocation()

        onLocationSelected()

    }

    @SuppressLint("MissingPermission")
    fun locate() :Task<Location>
    {
      val  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
         return    fusedLocationProviderClient.lastLocation

    }






    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType =GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType =GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType =GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType =GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }



}
