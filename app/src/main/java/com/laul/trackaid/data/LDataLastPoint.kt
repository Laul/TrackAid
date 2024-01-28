package com.laul.trackaid

data class LDataLastPoint(var date: String, var value: Float )
data class LDataAxis(var dateMillis: Long, var dateString: String, var dateIndex: Int)
data class LDataStats(var min: Float, var max: Float, var avg: Float)

data class LDataSerie(val x: ArrayList<Float>, val y: ArrayList<Float>)
data class LDataSeries(
    var s_all: LDataSerie,
    var s_sumD: LDataSerie,
    var s_sumH: LDataSerie,
    var s_min: LDataSerie,
    var s_max: LDataSerie,
    var s_avg: LDataSerie,
    )