package com.filipwieland.tenktrains.models

sealed class DepartureSnapshotMetricInfo
class WeightedAverageMetric(val field: String, val weight: String) : DepartureSnapshotMetricInfo()
class SumMetric(val field: String) : DepartureSnapshotMetricInfo()

enum class DepartureSnapshotMetric {
    DELAY {
        override fun metricInfo() = WeightedAverageMetric("avgDelay", "numDelayed")
    },
    CANCELLATIONS {
        override fun metricInfo() = SumMetric("numCancelled")
    }
    ;

    abstract fun metricInfo(): DepartureSnapshotMetricInfo
}
