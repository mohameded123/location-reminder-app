package com.udacity.project4.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.udacity.project4.MyApp
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*
import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
 private  val requestcode = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
        var b  = findViewById<Button>(R.id.login)
        b.setOnClickListener{ login() }


//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
     //var result =    IdpResponse.fromResultIntent(data)
        if(requestCode == requestcode)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                (application as MyApp).user  = FirebaseAuth.getInstance().currentUser
            var i  = Intent(this , RemindersActivity::class.java)
           startActivity(i)


            }



        }
    }

//    override fun onNavigateUp(): Boolean {
//
//        return super.onNavigateUp()
//    }

    fun login()
    {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )


        startActivityForResult(
            AuthUI. getInstance() .createSignInIntentBuilder() .setAvailableProviders(
                providers
            ).setTheme(R.style.AppTheme) .build(), requestcode
        )
    }



}
