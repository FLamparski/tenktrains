package com.filipwieland.tenktrains.repo

import co.elastic.clients.elasticsearch.ElasticsearchClient
import com.filipwieland.tenktrains.BaseIntegrationTest
import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch
import com.filipwieland.tenktrains.dao.SearchFilter
import com.filipwieland.tenktrains.extensions.search
import com.filipwieland.tenktrains.models.DepartureSnapshot
import com.filipwieland.tenktrains.models.DepartureSnapshotMetric
import io.kotest.matchers.shouldBe
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class ElasticsearchTimelineRepoTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var esClient: ElasticsearchClient

    @Autowired
    private lateinit var timelineRepo: ElasticsearchTimelineRepo

    @BeforeEach
    fun beforeEach() {
        setupIndex()
        esClient.deleteByQuery {
            it.index(Indices.DEPARTURE_SNAPSHOTS_INDEX)
            it.query {
                it.matchAll { it }
            }
        }
    }

    @ParameterizedTest(name = "should compute the timeline for metric {0}")
    @MethodSource("shouldComputeTheTimelineParameters")
    fun `should compute the timeline`(
        metric: DepartureSnapshotMetric,
        expectedValues: List<Double>
    ) {
        // given
        listOf(
            DepartureSnapshot(
                id = "LOL:1000",
                crs = "LOL",
                roundId = 1000,
                hasData = true,
                avgDelay = 0.0,
                numDelayed = 0,
                numCancelled = 0,
                numTrains = 5,
            ),
            DepartureSnapshot(
                id = "LOL:2000",
                crs = "LOL",
                roundId = 2000,
                hasData = true,
                avgDelay = 10.0,
                numDelayed = 1,
                numCancelled = 1,
                numTrains = 5,
            )
        ).forEach { doc ->
            esClient.index<DepartureSnapshot> {
                it.index(Indices.DEPARTURE_SNAPSHOTS_INDEX)
                it.document(doc)
                it.id(doc.id)
            }
        }

        await atMost Duration.ofSeconds(5) until {
            esClient.search<DepartureSnapshot> {
                it.index(Indices.DEPARTURE_SNAPSHOTS_INDEX)
                it.query {
                    it.matchAll { it }
                }
            }.hits().total().value() == 2L
        }

        // when
        val search = DepartureSnapshotSearch(
            filter = SearchFilter(),
            startTime = 0,
            endTime = 3000,
        )
        val timeline = timelineRepo.getTimeline(metric.metricInfo(), search)

        // then
        timeline.map { it.timestamp } shouldBe listOf(1000L, 2000L)
        timeline.map { it.value } shouldBe expectedValues
        timeline.map { it.count } shouldBe listOf(5L, 5L)
    }

    companion object {
        @JvmStatic
        fun shouldComputeTheTimelineParameters() = listOf(
            Arguments.of(DepartureSnapshotMetric.DELAY, listOf(0.0, 10.0)),
            Arguments.of(DepartureSnapshotMetric.CANCELLATIONS, listOf(0.0, 1.0))
        )
    }
}
