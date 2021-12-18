package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.events.DepartureSnapshotsReadyEvent
import com.filipwieland.tenktrains.extensions.Slf4j
import com.filipwieland.tenktrains.extensions.logger
import com.filipwieland.tenktrains.repo.DepartureSnapshotRepo
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class Persister(
    private val snapshotRepo: DepartureSnapshotRepo
) : ApplicationListener<DepartureSnapshotsReadyEvent>, Slf4j {
    override fun onApplicationEvent(event: DepartureSnapshotsReadyEvent) {
        event.dataPoints.forEach {
            try {
                snapshotRepo.save(it)
            }
            catch (e: Exception) {
                logger().error("Error saving ${it.id}", e)
            }
        }
    }
}
