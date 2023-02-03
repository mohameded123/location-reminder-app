package com.udacity.project4.locationreminders

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.BuildConfig
import com.udacity.project4.MyApp
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment
import kotlinx.android.synthetic.main.activity_reminders.*
import kotlin.math.log

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode== 100  )
        {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                Snackbar.make(
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.view!!,
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_LONG
                ).show()
            }
            else
            {
                ( ( (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).childFragmentManager.fragments[0]) as SelectLocationFragment ) .enableMyLocation()
            }


        }
        if(requestCode== 12345  )
        { var k = true
            for (i in grantResults.indices)
            {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                {
                    k = false
                    Snackbar.make(
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.view!!,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
                }

            }
            if(k)
                ( ( (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).childFragmentManager.fragments[0]) as SaveReminderFragment ) .enablelocation()



        }






    }

    override fun onStart() {
        super.onStart()
        if((application as MyApp).user ==null)
        {
            val i =Intent(this , AuthenticationActivity::class.java)
            startActivity(i)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }

       return super.onOptionsItemSelected(item)

    }


}
