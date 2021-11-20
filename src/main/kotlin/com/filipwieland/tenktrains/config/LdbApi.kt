package com.filipwieland.tenktrains.config

import org.springframework.stereotype.Component
import xjc.nationalrail.ldb.AccessToken
import xjc.nationalrail.ldb.GetBoardRequestParams
import xjc.nationalrail.ldb.Ldb

@Component
class LdbApi(private val config: LdbApiConfig) {
    private val ldb = Ldb().ldbServiceSoap12

    fun getDepartures(params: GetBoardRequestParams) = ldb.getDepartureBoard(params, accessToken())

    private fun accessToken() = AccessToken().apply {
        tokenValue = config.token
    }
}