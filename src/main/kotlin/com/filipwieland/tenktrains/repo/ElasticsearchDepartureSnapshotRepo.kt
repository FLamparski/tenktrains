package com.filipwieland.tenktrains.repo

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import com.filipwieland.tenktrains.extensions.Slf4j
import com.filipwieland.tenktrains.extensions.logger
import com.filipwieland.tenktrains.extensions.search
import com.filipwieland.tenktrains.models.DepartureSnapshot
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
class ElasticsearchDepartureSnapshotRepo(
    private val elasticsearchApi: ElasticsearchClient
) : Slf4j, DepartureSnapshotRepo {
    override fun save(snapshot: DepartureSnapshot) {
        logger().debug("Saving document {}", snapshot.id)
        val result = elasticsearchApi.index<DepartureSnapshot> {
            it.index(INDEX_NAME)
            it.id(snapshot.id)
            it.document(snapshot)
            it.timeout {
                it.time("1s")
            }
        }
        logger().debug("Saved {}: {}", result.id(), result.result().name)
    }

    override fun getAll(): List<DepartureSnapshot> = elasticsearchApi.search<DepartureSnapshot> {
        it.query(Query.of {
            it.matchAll { it }
        })
        it.index(INDEX_NAME)
        it.size(100)
    }.hits().hits().mapNotNull { it.source() }

    companion object {
        const val INDEX_NAME = "departure-snapshots"
    }
}
