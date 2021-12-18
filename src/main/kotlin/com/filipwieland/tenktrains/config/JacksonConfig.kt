package com.filipwieland.tenktrains.config

import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class JacksonConfig {
    @Bean
    fun xmlGregorianCalendarDeserializerCustomizer() = Jackson2ObjectMapperBuilderCustomizer {
        it.postConfigurer {
            it.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        }
    }
}
