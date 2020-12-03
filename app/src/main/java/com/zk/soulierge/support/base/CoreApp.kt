package com.zk.soulierge.support.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

open class CoreApp : MultiDexApplication(), Application.ActivityLifecycleCallbacks {
    var currentActivity: Activity? = null

    companion object {
        lateinit var INSTANCE: CoreApp

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        registerActivityLifecycleCallbacks(this)
    }

    override fun onTerminate() {
        unregisterActivityLifecycleCallbacks(this)
        super.onTerminate()
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityDestroyed(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}