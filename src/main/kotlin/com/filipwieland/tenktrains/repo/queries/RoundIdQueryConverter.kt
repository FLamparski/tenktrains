package com.filipwieland.tenktrains.repo.queries

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.json.JsonData
import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch

object RoundIdQueryConverter {
    fun convertToQuery(options: DepartureSnapshotSearch) = Query.of {
        it.range {
            it.field("roundId")
            it.gte(JsonData.of(options.startTime))
            it.lt(JsonData.of(options.endTime))
        }
    }
}
