package com.filipwieland.tenktrains.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients

@Configuration
class ElasticsearchConfig {
    @Bean
    fun elasticsearchRestConfig(properties: ElasticsearchProperties) = ClientConfiguration.builder()
        .connectedTo(properties.uri)
        .apply {
            val username = properties.username
            val password = properties.password
            if (username != null && password != null) {
                withBasicAuth(username, password)
            }
        }.build()

    @Bean
    fun elasticsearchRestClient(elasticsearchRestConfig: ClientConfiguration) =
        RestClients.create(elasticsearchRestConfig).rest()
}

@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
class ElasticsearchProperties {
    var uri: String? = null
    var username: String? = null
    var password: String? = null
}
