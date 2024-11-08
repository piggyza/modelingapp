package com.example.modelbookingapp.utils

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceMonitor @Inject constructor() {
    private val performance = FirebasePerformance.getInstance()
    private val traces = mutableMapOf<String, Trace>()

    fun startTrace(traceName: String) {
        val trace = performance.newTrace(traceName)
        trace.start()
        traces[traceName] = trace
    }

    fun stopTrace(traceName: String) {
        traces[traceName]?.stop()
        traces.remove(traceName)
    }

    fun incrementMetric(traceName: String, metricName: String) {
        traces[traceName]?.incrementMetric(metricName, 1)
    }

    fun putMetric(traceName: String, metricName: String, value: Long) {
        traces[traceName]?.putMetric(metricName, value)
    }
}