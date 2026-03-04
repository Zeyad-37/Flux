package com.zeyadgasser.core.v3

interface AnalyticsService {
    fun track(trackData: Track)
    fun setUser(userEmail: String)
}

/**
 * Track is the analytics event contract.
 */
interface Track {
    val eventData: Map<String, String>
    val eventName: String
}
