package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.renderscript.Sampler.Value
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private   val  requestcode = 100
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }
    lateinit var   d : ReminderDataItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location

            _viewModel.navigationCommand.value = NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
            d = ReminderDataItem(title , description ,location , latitude , longitude)


            wrapEspressoIdlingResource {

                enablelocation()

            }


        }
    }


   private val  radius = 200f



    private   fun  location_access(re : Boolean = true)
    {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())
        wrapEspressoIdlingResource {


        locationSettingsResponseTask.addOnFailureListener {
                exception ->
            if (exception is ResolvableApiException && re) {
                try {

                    startIntentSenderForResult(exception.getResolution().getIntentSender(), 1, null, 0, 0, 0, null)
                } catch (sendEx: IntentSender.SendIntentException) {



                }



            }else
                _viewModel.showSnackBar.value = "couldnot add geofence  please enable gps"

        }
        locationSettingsResponseTask.addOnCompleteListener {

            if(it.isSuccessful)
            {
                _viewModel.validateAndSaveReminder(d)
                if(_viewModel.selectedPOI.value !=null )
                    getGeofencingRequest(d)
            }







        }

    }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1)
        {
            location_access(false)

        }
    }



    fun enablelocation()
    {


        if (ispermissiongranted()) {

            location_access()

        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    12345
                )
            }
            else
            {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION ),
                    1234567890
                )
            }



        }

    }

    fun  ispermissiongranted() :Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==PackageManager.PERMISSION_GRANTED &&
                            requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED
        } else {
            requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED
        }
    }





    @SuppressLint("MissingPermission")
    private fun getGeofencingRequest(r :ReminderDataItem) //: GeofencingRequest
    {

            val geofencePendingIntent: PendingIntent by lazy {
                val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
                //intent.putExtra("id" , r.id)

                PendingIntent.getBroadcast(requireActivity(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            }
            val b = Geofence.Builder()

                .setRequestId(r.id)


                .setCircularRegion(
                    _viewModel.latitude.value!!,
                    _viewModel.longitude.value!!,
                    radius

                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
            val re = GeofencingRequest.Builder().apply {
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                addGeofences(listOf(b))
            }.build()
            val geofencingClient = LocationServices.getGeofencingClient(requireActivity())
            geofencingClient?.addGeofences(re, geofencePendingIntent)



    }
    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
}
