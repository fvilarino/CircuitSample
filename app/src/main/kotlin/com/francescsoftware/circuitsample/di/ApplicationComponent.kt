package com.francescsoftware.circuitsample.di

import android.app.Application
import com.francescsoftware.circuitsample.CircuitApplication
import com.francescsoftware.circuitsample.MainActivity
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
interface ApplicationComponent {

    fun inject(application: CircuitApplication)

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    fun interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}
