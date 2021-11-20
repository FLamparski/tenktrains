package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.events.DepartureSnapshotsReadyEvent
import com.filipwieland.tenktrains.events.PollCompletedEvent
import com.filipwieland.tenktrains.models.DepartureSnapshot
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import xjc.nationalrail.ldb.ArrayOfServiceItems4
import xjc.nationalrail.ldb.ServiceItem4
import xjc.nationalrail.ldb.StationBoard4

@ExtendWith(MockKExtension::class)
internal class EnricherTest {
    @RelaxedMockK
    lateinit var applicationEventPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var sut: Enricher

    @Test
    fun `should compute metrics for board`() {
        // GIVEN
        val inputEvent = PollCompletedEvent(listOf(
            dep().copy(
                hasData = true,
                board = StationBoard4().apply {
                    trainServices = ArrayOfServiceItems4()
                    trainServices.service.add(ServiceItem4().apply {
                        etd = "On time"
                        std = "13:00"
                    })
                    trainServices.service.add(ServiceItem4().apply {
                        delayReason = "some delay reason"
                        etd = "13:10"
                        std = "13:00"
                    })
                    trainServices.service.add(ServiceItem4().apply {
                        cancelReason = "some cancel reason"
                        etd = "Canceled"
                        std = "13:00"
                    })
                }
            )
        ))

        // WHEN
        sut.onApplicationEvent(inputEvent)

        // THEN
        val outputEventSlot = slot<DepartureSnapshotsReadyEvent>()
        verify {
            applicationEventPublisher.publishEvent(capture(outputEventSlot))
        }
        outputEventSlot.captured.dataPoints.size shouldBe 1

        // AND
        val enrichedDataPoint = outputEventSlot.captured.dataPoints[0]
        enrichedDataPoint.numTrains shouldBe 3
        enrichedDataPoint.numCancelled shouldBe 1
        enrichedDataPoint.numDelayed shouldBe 1
        enrichedDataPoint.avgDelay shouldBe (5.0 plusOrMinus 0.001)
    }

    fun dep() = DepartureSnapshot.new("XYZ", 0)
}