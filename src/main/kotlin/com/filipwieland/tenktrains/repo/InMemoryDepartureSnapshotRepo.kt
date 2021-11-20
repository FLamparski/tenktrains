package com.filipwieland.tenktrains.repo

import com.filipwieland.tenktrains.models.DepartureSnapshot
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class InMemoryDepartureSnapshotRepo : DepartureSnapshotRepo {
    private val snapshots: MutableList<DepartureSnapshot> = mutableListOf()

    override fun save(snapshot: DepartureSnapshot) {
        snapshots.add(snapshot)
    }

    override fun getAll() = snapshots
}
