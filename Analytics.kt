package com.example.modelbookingapp.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Analytics @Inject constructor() {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logLogin(method: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
    }

    fun logSignUp(method: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params)
    }

    fun logBookingCreated(modelId: String) {
        val params = Bundle().apply {
            putString("model_id", modelId)
        }
        firebaseAnalytics.logEvent("booking_created", params)
    }

    fun logBookingStatusChanged(bookingId: String, newStatus: String) {
        val params = Bundle().apply {
            putString("booking_id", bookingId)
            putString("new_status", newStatus)
        }
        firebaseAnalytics.logEvent("booking_status_changed", params)
    }

    fun logProfileUpdate(userId: String) {
        val params = Bundle().apply {
            putString("user_id", userId)
        }
        firebaseAnalytics.logEvent("profile_updated", params)
    }
}