package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.events.DepartureSnapshotsReadyEvent
import com.filipwieland.tenktrains.events.PollCompletedEvent
import com.filipwieland.tenktrains.extensions.Slf4j
import com.filipwieland.tenktrains.models.DepartureSnapshot
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class Enricher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : Slf4j, ApplicationListener<PollCompletedEvent> {

    override fun onApplicationEvent(event: PollCompletedEvent) {
        val enrichedDepartures = event.dataPoints.map { point ->
            when (point.hasData) {
                true -> enrichPoint(point)
                false -> point
            }
        }
        applicationEventPublisher.publishEvent(DepartureSnapshotsReadyEvent(enrichedDepartures))
    }

    private fun enrichPoint(point: DepartureSnapshot): DepartureSnapshot {
        val trains = point.board!!.trainServices.service
        val delays = trains.mapNotNull {
            if (ON_TIME == it.etd) {
                0.0
            }
            else if (it.etd.matches("\\d{2}:\\d{2}".toRegex())) {
                getMinutesDifference(it.std, it.etd)
            }
            else {
                null
            }
        }
        return point.copy(
            numTrains = trains.size.toLong(),
            numCancelled = trains.count { it.cancelReason != null }.toLong(),
            numDelayed = delays.count { it > 0.0 }.toLong(),
            avgDelay = delays.average()
        )
    }

    private fun getMinutesDifference(std: String?, etd: String?): Double? {
        if (std == null || etd == null) {
            return null
        }
        return getMinutesFromTimeString(etd) - getMinutesFromTimeString(std)
    }

    private fun getMinutesFromTimeString(str: String): Double {
        val (hours, minutes) = str.split(":")
        return hours.toInt() * 60.0 + minutes.toInt()
    }

    companion object {
        const val ON_TIME = "On time"
    }
}