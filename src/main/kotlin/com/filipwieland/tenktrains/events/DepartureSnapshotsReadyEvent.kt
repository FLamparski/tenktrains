package com.filipwieland.tenktrains.events

import com.filipwieland.tenktrains.models.DepartureSnapshot
import org.springframework.context.ApplicationEvent

data class DepartureSnapshotsReadyEvent(
    val dataPoints: List<DepartureSnapshot>
) : ApplicationEvent(dataPoints)