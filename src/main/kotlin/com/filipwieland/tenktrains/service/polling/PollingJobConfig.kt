package com.filipwieland.tenktrains.service.polling

import co.elastic.clients.elasticsearch.watcher.TriggerBuilders
import com.filipwieland.tenktrains.config.PollingConfig
import org.quartz.*
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import javax.annotation.PostConstruct

@Configuration
internal class PollingJobConfig {
    @Bean
    fun pollingJobDetails() = JobBuilder
        .newJob()
        .ofType(PollingJob::class.java)
        .storeDurably()
        .withIdentity("LWDBSPoll")
        .withDescription("Polls departure boards from LWDBS and emits an app event when done")
        .build()

    @Bean
    fun pollingJobTrigger(pollingJobDetail: JobDetail, pollingConfig: PollingConfig) = TriggerBuilder
        .newTrigger()
        .forJob(pollingJobDetail)
        .withSchedule(simpleSchedule().repeatForever().withIntervalInMilliseconds(pollingConfig.intervalMs))
        .build()
}
