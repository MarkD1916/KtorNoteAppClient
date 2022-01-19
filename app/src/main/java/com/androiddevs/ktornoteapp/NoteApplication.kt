package com.androiddevs.ktornoteapp

import android.app.Application
import com.vmakd1916gmail.com.login_logout_register.api.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NoteApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        NetworkMonitor(this).startNetworkCallback()
    }

    override fun onTerminate(){
        super.onTerminate()
        NetworkMonitor(this).stopNetworkCallback()
    }
}