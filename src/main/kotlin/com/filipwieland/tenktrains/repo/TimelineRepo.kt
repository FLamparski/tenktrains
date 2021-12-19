package com.filipwieland.tenktrains.repo

import com.filipwieland.tenktrains.dao.DepartureSnapshotSearch
import com.filipwieland.tenktrains.models.DepartureSnapshotMetricInfo
import com.filipwieland.tenktrains.models.TimelineDataPoint

interface TimelineRepo {
    fun getTimeline(metricInfo: DepartureSnapshotMetricInfo, options: DepartureSnapshotSearch): List<TimelineDataPoint>
}
