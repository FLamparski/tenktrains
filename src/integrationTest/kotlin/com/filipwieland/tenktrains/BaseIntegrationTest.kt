package com.filipwieland.tenktrains

import co.elastic.clients.elasticsearch.ElasticsearchClient
import com.filipwieland.tenktrains.repo.Indices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TenktrainsApplication::class, IntegrationTestConfig::class]
)
@ActiveProfiles("test")
@Testcontainers
abstract class BaseIntegrationTest {
    @Autowired
    private lateinit var esClient: ElasticsearchClient

    protected fun setupIndex() {
        val indicesClient = esClient.indices()
        indicesClient
            .exists { it.index(Indices.DEPARTURE_SNAPSHOTS_INDEX) }
            .value()
            .takeUnless { it }
            ?.let {
                indicesClient.create {
                    it.index(Indices.DEPARTURE_SNAPSHOTS_INDEX)
                }
            }
    }

    companion object {
        @JvmStatic
        @Container
        private val elasticsearchContainer = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.16.1")

        @JvmStatic
        @DynamicPropertySource
        fun setTestProperties(registry: DynamicPropertyRegistry) {
            registry.add("elasticsearch.uri", elasticsearchContainer::getHttpHostAddress)
        }
    }
}
