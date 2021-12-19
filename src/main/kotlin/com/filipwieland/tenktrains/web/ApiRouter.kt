package com.filipwieland.tenktrains.web

import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch
import com.filipwieland.tenktrains.repo.DepartureSnapshotRepo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router

@Configuration
class ApiRouter(
    private val departureSnapshotRepo: DepartureSnapshotRepo,
    private val timelineHandler: TimelineHandler,
) {
    @Bean
    fun route() = router {
        GET("/test") {
            ServerResponse.ok().body(fromValue(departureSnapshotRepo.getAll()))
        }

        (accept(MediaType.APPLICATION_JSON) and "/boards").nest {
            GET("/{crs}/{roundId}") {
                departureSnapshotRepo.findOneByCrsAndRoundId(it.pathVariable("crs"), it.pathVariable("roundId").toLong())
                    .map { ServerResponse.ok().body(fromValue(it)) }
                    .orElseGet { ServerResponse.notFound().build() }
            }
        }

        (accept(MediaType.APPLICATION_JSON) and "/timelines").nest {
            POST("/{metricId}").invoke(timelineHandler::getTimelineForMetric)
        }
    }
}
