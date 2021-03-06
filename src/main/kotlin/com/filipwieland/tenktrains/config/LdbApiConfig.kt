package com.filipwieland.tenktrains.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xjc.nationalrail.ldb.Ldb

@Configuration
class LdbApiConfig {
    @Bean
    fun ldbApi() = Ldb().ldbServiceSoap12
}

@Configuration
@ConfigurationProperties(
    prefix = "ldb"
)
class LdbApiProperties {
    var token: String? = null
}