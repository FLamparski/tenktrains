package com.filipwieland.tenktrains.web

import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch
import com.filipwieland.tenktrains.models.DepartureSnapshotMetric
import com.filipwieland.tenktrains.repo.TimelineRepo
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Component
class TimelineHandler(
    private val timelineRepo: TimelineRepo
) {
    fun getTimelineForMetric(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono<DepartureSnapshotSearch>().flatMap { options ->
            val metric = request.pathVariable("metricId").let { DepartureSnapshotMetric.valueOf(it) }
            val timeline = timelineRepo.getTimeline(metric.metricInfo(), options)
            ServerResponse.ok().bodyValue(timeline)
        }
    }
}
