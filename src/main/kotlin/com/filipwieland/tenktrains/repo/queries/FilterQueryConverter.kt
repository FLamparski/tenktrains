package com.filipwieland.tenktrains.repo.queries

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch

object FilterQueryConverter {
    fun convertToQuery(options: DepartureSnapshotSearch) = Query.of {
        val filterQueries = listOf(
            options.filter.crs?.let { filterValues ->
                Query.of {
                    it.terms {
                        it.field("crs.keyword")
                        it.terms {
                            it.value(filterValues.map { FieldValue.of(it) })
                        }
                    }
                }
            }
        )
        it.bool {
            it.must(filterQueries.filterNotNull())
        }
    }
}
