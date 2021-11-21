package com.filipwieland.tenktrains.repo

import com.filipwieland.tenktrains.extensions.Slf4j
import com.filipwieland.tenktrains.extensions.logger
import com.filipwieland.tenktrains.models.DepartureSnapshot
import org.springframework.context.annotation.Primary
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Repository

@Primary
@Repository
class ElasticsearchDepartureSnapshotRepo(
    private val elasticsearchOperations: ElasticsearchOperations
) : Slf4j, DepartureSnapshotRepo {
    override fun save(snapshot: DepartureSnapshot) {
        logger().debug("Saving document {}", snapshot.id)
        elasticsearchOperations.save(snapshot, IndexCoordinates.of(INDEX_NAME))
    }

    override fun getAll(): List<DepartureSnapshot> =
        elasticsearchOperations.search(
            Query.findAll(),
            DepartureSnapshot::class.java,
            IndexCoordinates.of(INDEX_NAME)
        ).searchHits.map { it.content }

    companion object {
        const val INDEX_NAME = "departure-snapshots"
    }
}