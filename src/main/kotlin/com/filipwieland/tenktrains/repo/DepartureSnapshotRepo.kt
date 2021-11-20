package com.filipwieland.tenktrains.repo

import com.filipwieland.tenktrains.models.DepartureSnapshot

interface DepartureSnapshotRepo {
    fun save(snapshot: DepartureSnapshot)

    fun getAll(): List<DepartureSnapshot>
}
