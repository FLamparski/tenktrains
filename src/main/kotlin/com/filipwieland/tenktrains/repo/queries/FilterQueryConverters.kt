package com.filipwieland.tenktrains.repo.queries

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch

object FilterQueryConverters {
    private val converters = listOf<FilterConverter>(
        { options: DepartureSnapshotSearch -> // Filter: CRS
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
        }
    )

    fun convertToQuery(options: DepartureSnapshotSearch) = Query.of {
        it.bool {
            it.must(converters.mapNotNull { it(options) })
        }
    }
}

typealias FilterConverter = (DepartureSnapshotSearch) -> Query?
