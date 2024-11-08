package com.example.modelbookingapp.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashReporter @Inject constructor() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    fun logException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    fun log(message: String) {
        crashlytics.log(message)
    }

    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
}