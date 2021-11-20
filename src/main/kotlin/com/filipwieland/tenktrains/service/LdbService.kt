package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.config.LdbApiProperties
import org.springframework.stereotype.Component
import xjc.nationalrail.ldb.AccessToken
import xjc.nationalrail.ldb.GetBoardRequestParams
import xjc.nationalrail.ldb.LDBServiceSoap
import xjc.nationalrail.ldb.Ldb

@Component
class LdbService(private val config: LdbApiProperties, private val ldb: LDBServiceSoap) {
    fun getDepartures(params: GetBoardRequestParams) = ldb.getDepartureBoard(params, accessToken()).getStationBoardResult

    private fun accessToken() = AccessToken().apply {
        tokenValue = config.token
    }
}