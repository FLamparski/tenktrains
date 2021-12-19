package com.filipwieland.tenktrains.models

data class TimelineDataPoint(
    val timestamp: Long,
    val value: Double?,
    val count: Long?,
)
