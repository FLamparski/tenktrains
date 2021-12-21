package com.filipwieland.tenktrains

import com.filipwieland.tenktrains.service.LdbService
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class IntegrationTestConfig {
    @Bean
    @Primary
    fun ldbService() = mockk<LdbService>()
}
