package com.filipwieland.tenktrains.models

import xjc.nationalrail.ldb.StationBoard4
import javax.persistence.Id

data class DepartureSnapshot(
    @Id
    val id: String,
    val crs: String,
    val roundId: Long,
    val hasData: Boolean,
    val numTrains: Long? = null,
    val numDelayed: Long? = null,
    val avgDelay: Double? = null,
    val numCancelled: Long? = null,
    val board: StationBoard4? = null,
) {
    companion object {
        fun new(crs: String, roundId: Long) = DepartureSnapshot(
            crs = crs,
            roundId = roundId,
            id = "$crs:$roundId",
            hasData = false,
        )
    }
}
