package com.laul.trackaid

data class LDataPoint(var date: String , var value: ArrayList<Float> )
data class LDataAxis(var dateMillis: Long, var dateString: String, var dateIndex: Int)
data class LDataStats(var min: Float, var max: Float, var avg: Float)

