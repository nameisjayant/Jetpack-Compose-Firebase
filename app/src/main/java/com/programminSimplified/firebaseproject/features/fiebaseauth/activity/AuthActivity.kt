package com.programminSimplified.firebaseproject.features.fiebaseauth.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import com.programminSimplified.firebaseproject.features.fiebaseauth.AuthScreen
import com.programminSimplified.firebaseproject.features.fiebaseauth.screens.PhoneAuthScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            androidx.compose.material.Surface() {
                Scaffold {
                //    AuthScreen()
                    PhoneAuthScreen(activity = this)
                }
            }
        }
    }
}