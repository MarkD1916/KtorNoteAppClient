package com.androiddevs.ktornoteapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.databinding.ActivityMainBinding
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import com.androiddevs.ktornoteapp.ui.auth.AuthFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private var _binding: ActivityMainBinding? = null
    val mBinding get() = _binding!!

    @Inject
    lateinit var basicAuthSharedPreferences: BasicAuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        navController = Navigation.findNavController(this, R.id.navHostFragment)

        loginIfAuth()
    }

    private fun loginIfAuth() {
        if (basicAuthSharedPreferences.getStoredEmail().isNotEmpty() && basicAuthSharedPreferences.getStoredPassword().isNotEmpty()){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.authFragment, true)
                .build()
            navController.navigate(AuthFragmentDirections.actionAuthFragmentToNoteFragment(), navOptions)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

