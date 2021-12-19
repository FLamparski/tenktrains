package com.filipwieland.tenktrains.dao

data class DepartureSnapshotSearch(
    val filter: SearchFilter,
    val startTime: Long,
    val endTime: Long,
)
