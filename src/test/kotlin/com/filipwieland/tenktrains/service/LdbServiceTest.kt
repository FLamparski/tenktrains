package com.filipwieland.tenktrains.service

import com.filipwieland.tenktrains.config.LdbApiProperties
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xjc.nationalrail.ldb.AccessToken
import xjc.nationalrail.ldb.GetBoardRequestParams
import xjc.nationalrail.ldb.LDBServiceSoap

@ExtendWith(MockKExtension::class)
internal class LdbServiceTest {
    @MockK
    lateinit var config: LdbApiProperties

    @MockK(relaxed = true)
    lateinit var soapApi: LDBServiceSoap

    @InjectMockKs
    lateinit var sut: LdbService

    @Test
    fun `should specify the token for getDepartures`() {
        // GIVEN
        every {
            config.token
        } returns "some-token"
        val params = GetBoardRequestParams().apply {
            crs = "XYZ"
        }

        // WHEN
        sut.getDepartures(params)

        // THEN
        val accessTokenSlot = slot<AccessToken>()
        verify {
            soapApi.getDepartureBoard(params, capture(accessTokenSlot))
        }
        accessTokenSlot.captured.tokenValue shouldBe "some-token"
    }
}