package com.filipwieland.tenktrains.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

@Configuration
class ExecutorConfig(private val executorProperties: ExecutorProperties) {
    @Bean
    fun pollingExecutorService() = Executors.newScheduledThreadPool(executorProperties.pollingThreads)
}

@Configuration
@ConfigurationProperties(
    prefix = "executors"
)
class ExecutorProperties {
    var pollingThreads: Int = 1
}