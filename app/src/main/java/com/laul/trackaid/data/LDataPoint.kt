package com.laul.trackaid

data class LDataPoint(var dateMillis_bucket: Long,var dateMillis_point: Long, var value: ArrayList<Float> )
data class LDataAxis(var dateMillis: Long, var dateString: String, var dateIndex: Int)


