package com.filipwieland.tenktrains.repo.queries

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch
import com.filipwieland.tenktrains.dao.SearchFilter
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FilterQueryConverterTest {
    @Test
    fun `should convert an empty filter to an empty bool query`() {
        // given
        val search = DepartureSnapshotSearch(
            filter = SearchFilter(),
            startTime = 0,
            endTime = 0
        )

        // when
        val query = FilterQueryConverter.convertToQuery(search)

        // then
        query._kind() shouldBe Query.Kind.Bool
        query.bool().must() shouldHaveSize 0
    }

    @Test
    fun `should convert a CRS filter to a terms query`() {
        // given
        val search = DepartureSnapshotSearch(
            filter = SearchFilter(
                crs = listOf("LOL")
            ),
            startTime = 0,
            endTime = 0
        )

        // when
        val query = FilterQueryConverter.convertToQuery(search)

        // then
        query._kind() shouldBe Query.Kind.Bool
        query.bool().must() shouldHaveSize 1

        val termsQuery = query.bool().must()[0]
        termsQuery._kind() shouldBe Query.Kind.Terms
        termsQuery.terms().field() shouldBe "crs.keyword"
        termsQuery.terms().terms().value()[0].stringValue() shouldBe "LOL"
    }
}
