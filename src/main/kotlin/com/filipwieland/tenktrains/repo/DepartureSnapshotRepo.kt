package com.filipwieland.tenktrains.repo

import com.filipwieland.tenktrains.models.DepartureSnapshot
import java.util.*

interface DepartureSnapshotRepo {
    fun save(snapshot: DepartureSnapshot)

    fun getAll(): List<DepartureSnapshot>

    fun findOneByCrsAndRoundId(crs: String, roundId: Long): Optional<DepartureSnapshot>
}
