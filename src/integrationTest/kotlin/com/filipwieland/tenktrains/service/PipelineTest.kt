package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.BaseIntegrationTest
import com.filipwieland.tenktrains.extensions.Slf4j
import com.filipwieland.tenktrains.extensions.logger
import com.filipwieland.tenktrains.repo.ElasticsearchDepartureSnapshotRepo
import com.filipwieland.tenktrains.service.polling.PollingService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import org.awaitility.kotlin.await
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import xjc.nationalrail.ldb.ArrayOfServiceItems4
import xjc.nationalrail.ldb.StationBoard4
import java.time.Duration

class PipelineTest : BaseIntegrationTest(), Slf4j {
    @Autowired
    private lateinit var pollingService: PollingService

    @Autowired
    private lateinit var repo: ElasticsearchDepartureSnapshotRepo

    @Autowired
    private lateinit var ldbService: LdbService

    @Test
    fun `should persist departure boards`() {
        // given
        val board = StationBoard4().apply {
            crs = "LOL"
            trainServices = ArrayOfServiceItems4()
        }
        every {
            ldbService.getDepartures(any())
        } returns board

        // when
        pollingService.poll()

        // then
        await atMost Duration.ofSeconds(5) untilAsserted {
            val snapshots = repo.getAll()
            snapshots.isNotEmpty() shouldBe true
            snapshots[0].board shouldNotBe null
            snapshots[0].board!!.crs shouldBe "LOL"
        }
    }
}
