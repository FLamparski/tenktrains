package com.filipwieland.tenktrains.web

import com.filipwieland.tenktrains.repo.DepartureSnapshotRepo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class ApiRouter(private val departureSnapshotRepo: DepartureSnapshotRepo) {
    @Bean
    fun route() = router {
        GET("/test") {
            ServerResponse.ok().body(fromValue(departureSnapshotRepo.getAll()))
        }
    }
}
