package com.filipwieland.tenktrains.events

import com.filipwieland.tenktrains.models.DepartureSnapshot
import org.springframework.context.ApplicationEvent

data class PollCompletedEvent(
    val dataPoints: List<DepartureSnapshot>
) : ApplicationEvent(dataPoints)