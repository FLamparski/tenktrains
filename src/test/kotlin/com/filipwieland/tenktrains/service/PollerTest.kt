package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.config.PolledStationEntryProperties
import com.filipwieland.tenktrains.config.PollingConfig
import com.filipwieland.tenktrains.events.PollCompletedEvent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import xjc.nationalrail.ldb.ArrayOfServiceItems4
import xjc.nationalrail.ldb.GetBoardRequestParams
import xjc.nationalrail.ldb.ServiceItem4
import xjc.nationalrail.ldb.StationBoard4
import java.time.Clock
import java.util.concurrent.ScheduledExecutorService

@ExtendWith(MockKExtension::class)
internal class PollerTest {
    @RelaxedMockK
    lateinit var ldbService: LdbService

    @MockK
    lateinit var pollingConfig: PollingConfig

    @RelaxedMockK
    lateinit var pollingExecutorService: ScheduledExecutorService

    @RelaxedMockK
    lateinit var applicationEventPublisher: ApplicationEventPublisher

    @MockK
    lateinit var clock: Clock

    @InjectMockKs
    lateinit var sut: Poller

    @BeforeEach
    fun beforeEach() {
        every {
            clock.millis()
        } returns 0
        every {
            pollingConfig.intervalMs
        } returns 1000
    }

    @Test
    fun `should poll all configured stations`() {
        // GIVEN
        val crsCodes = listOf("XYZ", "ABC")
        every {
            pollingConfig.stations
        } returns crsCodes.map {
            PolledStationEntryProperties().apply {
                crs = it
            }
        }.toMutableList()

        // WHEN
        sut.poll()

        // THEN
        val getDeparturesParams = mutableListOf<GetBoardRequestParams>()
        verify(exactly = 2) {
            ldbService.getDepartures(capture(getDeparturesParams))
        }
        getDeparturesParams.map { it.crs } shouldBe crsCodes
    }

    @Test
    fun `should transform the raw departure board response and emit event`() {
        // GIVEN
        every {
            pollingConfig.stations
        } returns mutableListOf(
            PolledStationEntryProperties().apply {
                crs = "XYZ"
            }
        )
        every {
            ldbService.getDepartures(any())
        } returns StationBoard4().apply {
            crs = "XYZ"
            trainServices = ArrayOfServiceItems4().apply {
                service.add(ServiceItem4().apply {
                    rsid = "foo"
                })
            }
        }

        // WHEN
        sut.poll()

        // THEN
        val eventSlot = slot<PollCompletedEvent>()
        verify {
            applicationEventPublisher.publishEvent(capture(eventSlot))
        }
        eventSlot.captured.dataPoints.size shouldBe 1

        // AND
        val event = eventSlot.captured.dataPoints[0]
        event.id shouldBe "XYZ:0"
        event.crs shouldBe "XYZ"
        event.roundId shouldBe 0
        event.hasData shouldBe true
        event.board shouldNotBe null
        event.board!!.trainServices.service[0].rsid shouldBe "foo"
    }

    @Test
    fun `should emit departure board snapshots for failed requests too`() {
        // GIVEN
        every {
            pollingConfig.stations
        } returns mutableListOf(
            PolledStationEntryProperties().apply {
                crs = "XYZ"
            }
        )
        every {
            ldbService.getDepartures(any())
        } throws Exception("this is a test")

        // WHEN
        sut.poll()

        // THEN
        val eventSlot = slot<PollCompletedEvent>()
        verify {
            applicationEventPublisher.publishEvent(capture(eventSlot))
        }
        eventSlot.captured.dataPoints.size shouldBe 1

        // AND
        val event = eventSlot.captured.dataPoints[0]
        event.id shouldBe "XYZ:0"
        event.crs shouldBe "XYZ"
        event.roundId shouldBe 0
        event.hasData shouldBe false
        event.board shouldBe null
    }
}