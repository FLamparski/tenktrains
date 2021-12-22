package com.filipwieland.tenktrains.repo

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.SearchResponse
import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch
import com.filipwieland.tenktrains.extensions.Slf4j
import com.filipwieland.tenktrains.extensions.search
import com.filipwieland.tenktrains.models.DepartureSnapshotMetricInfo
import com.filipwieland.tenktrains.models.SumMetric
import com.filipwieland.tenktrains.models.TimelineDataPoint
import com.filipwieland.tenktrains.models.WeightedAverageMetric
import com.filipwieland.tenktrains.repo.queries.FilterQueryConverter
import com.filipwieland.tenktrains.repo.queries.RoundIdQueryConverter
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository
import java.util.*

@Primary
@Repository
class ElasticsearchTimelineRepo(
    private val elasticsearchApi: ElasticsearchClient,
) : Slf4j, TimelineRepo {
    override fun getTimeline(metricInfo: DepartureSnapshotMetricInfo, options: DepartureSnapshotSearch): List<TimelineDataPoint> {
        val query = Query.of {
            it.bool {
                it.must(listOf(
                    FilterQueryConverter.convertToQuery(options),
                    RoundIdQueryConverter.convertToQuery(options)
                ))
            }
        }

        val result = elasticsearchApi.search<DepartureSnapshotSearch> {
            it.query(query)
            it.size(0)
            it.aggregations(ROUND_ID_AGG) {
                it.terms {
                    it.field("roundId")
                    it.size(MAX_BUCKETS)
                }
                .aggregations("value") {
                    getValueAggregation(metricInfo, it)
                }
                .aggregations("count") {
                    it.sum {
                        it.field(NUM_TRAINS_FIELD)
                        it.missing(FieldValue.of(0))
                    }
                }
            }
            it.index(Indices.DEPARTURE_SNAPSHOTS_INDEX)
        }

        return extractResults(result)
    }

    private fun extractResults(result: SearchResponse<DepartureSnapshotSearch>): List<TimelineDataPoint> {
        return result.aggregations()[ROUND_ID_AGG]?.lterms()?.buckets()?.let { buckets ->
            val dataPoints = TreeMap<Long, TimelineDataPoint>()
            buckets.array().forEach { bucket ->
                val key = bucket.key().toLong()
                val dataPoint = extractDataPoint(key, bucket)
                dataPoints[key] = dataPoint
            }
            dataPoints.values.toList()
        } ?: listOf()
    }

    private fun extractDataPoint(
        roundId: Long,
        bucket: LongTermsBucket
    ): TimelineDataPoint {
        val count = bucket.aggregations()["count"]?.sum()?.value()?.toLong() ?: 0
        val value = bucket.aggregations()["value"]?.let { valueAgg ->
            if (valueAgg.isWeightedAvg) {
                valueAgg.weightedAvg().value()
            }
            else if (valueAgg.isSum) {
                valueAgg.sum().value()
            }
            else {
                null
            }
        }
        return TimelineDataPoint(
            timestamp = roundId,
            value = value,
            count = count
        )
    }

    private fun getValueAggregation(metricInfo: DepartureSnapshotMetricInfo, builder: Aggregation.Builder) =
        when (metricInfo) {
            is WeightedAverageMetric -> builder.weightedAvg {
                it.value {
                    it.field(metricInfo.field)
                    it.missing(0.0)
                }
                it.weight {
                    it.field(metricInfo.weight)
                    it.missing(0.0)
                }
            }
            is SumMetric -> builder.sum {
                it.field(metricInfo.field)
            }
        }

    companion object {
        const val MAX_BUCKETS = 10_000
        const val NUM_TRAINS_FIELD = "numTrains"
        const val ROUND_ID_AGG = "roundId"
    }
}
