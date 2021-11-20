package com.filipwieland.tenktrains.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@ConfigurationProperties(
    prefix = "polling"
)
class PollingConfig {
    var stations: MutableList<PolledStationEntryProperties> = mutableListOf()
    var intervalMs: Long = TimeUnit.MINUTES.toMillis(5)
}

class PolledStationEntryProperties {
    var crs: String? = null
    var pageSize: Int = 100
}
