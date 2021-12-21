package com.filipwieland.tenktrains

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
