package com.filipwieland.tenktrains.web

import com.filipwieland.tenktrains.config.LdbApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import xjc.nationalrail.ldb.GetBoardRequestParams
import xjc.nationalrail.ldb.Ldb

@Configuration
class ApiRouter(private val ldbApi: LdbApi) {
    @Bean
    fun route() = router {
        GET("/test") {
            val ldbRequest = ldbApi.getDepartures(GetBoardRequestParams().apply {
                crs = "DVP"
                numRows = 20
            })
            val ldbResult = ldbRequest.getStationBoardResult
            ServerResponse.ok().body(fromValue(ldbResult))
        }
    }
}