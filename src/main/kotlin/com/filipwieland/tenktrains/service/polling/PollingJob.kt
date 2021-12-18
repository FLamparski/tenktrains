package com.filipwieland.tenktrains.service.polling

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Component

@Component
internal class PollingJob(
    private val pollingService: PollingService
) : Job {
    override fun execute(context: JobExecutionContext?) = pollingService.poll()
}
