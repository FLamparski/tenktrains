package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.config.PollingConfig
import com.filipwieland.tenktrains.events.PollCompletedEvent
import com.filipwieland.tenktrains.extensions.Slf4j
import com.filipwieland.tenktrains.extensions.logger
import com.filipwieland.tenktrains.models.DepartureSnapshot
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import xjc.nationalrail.ldb.GetBoardRequestParams
import xjc.nationalrail.ldb.StationBoard4
import java.time.Clock
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import kotlin.streams.toList

@Service
class Poller(
    private val ldbService: LdbService,
    private val pollingConfig: PollingConfig,
    private val pollingExecutorService: ScheduledExecutorService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val clock: Clock
) : Slf4j {
    @PostConstruct
    fun start() {
        pollingExecutorService.scheduleAtFixedRate(this::poll, 0, pollingConfig.intervalMs, TimeUnit.MILLISECONDS)
    }

    @PreDestroy
    fun stop() {
        pollingExecutorService.shutdown()
    }

    fun poll() {
        logger().info("Poll started")
        val nowRoundId = getRoundId()
        val batch = pollingConfig.stations.stream()
            .map { station ->
                GetBoardRequestParams().apply {
                    crs = station.crs
                    numRows = station.pageSize
                }
            }
            .map { params ->
                try {
                    PollSuccess(ldbService.getDepartures(params), params.crs)
                }
                catch (e: Exception) {
                    logger().error("Error polling {}", params.crs, e)
                    PollFailed(params.crs)
                }
            }
            .toList()
        logger().info(
            "Poll done ({} total, {} success, {} fail)",
            batch.size,
            batch.count { it is PollSuccess },
            batch.count { it is PollFailed }
        )
        val dataPoints = batch.map {
            val point = DepartureSnapshot.new(it.crs, nowRoundId)
            when (it) {
                is PollSuccess -> point.copy(hasData = true, board = it.result)
                is PollFailed -> point.copy(hasData = false)
            }
        }
        applicationEventPublisher.publishEvent(PollCompletedEvent(dataPoints))
    }

    fun getRoundId(): Long {
        val now = clock.millis()
        return now - (now % pollingConfig.intervalMs)
    }

    sealed class PollResult(val crs: String)
    class PollFailed(crs: String) : PollResult(crs)
    class PollSuccess(val result: StationBoard4, crs: String) : PollResult(crs)
}