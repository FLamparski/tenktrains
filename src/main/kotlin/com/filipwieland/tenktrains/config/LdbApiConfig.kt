package com.filipwieland.tenktrains.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(
    prefix = "ldb"
)
class LdbApiConfig {
    var token: String? = null
}