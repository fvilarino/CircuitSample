package com.francescsoftware.circuitsample

import android.app.Application
import com.francescsoftware.circuitsample.di.ApplicationComponent
import com.francescsoftware.circuitsample.di.DaggerApplicationComponent

class CircuitApplication : Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.factory().create(this).apply {
            inject(this@CircuitApplication)
        }
    }
}
