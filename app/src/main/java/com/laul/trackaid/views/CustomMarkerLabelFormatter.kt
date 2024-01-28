package com.laul.trackaid.views

//public object CustomMarkerLabelFormatter : MarkerLabelFormatter {
//
//    private const val PATTERN = "%.02f"
//
//    override fun getLabel(
//        markedEntries: List<Marker.EntryModel>,
//        chartValues: ChartValues,
//    ): CharSequence = markedEntries.transformToSpannable(
//        prefix = if (markedEntries.size > 1) {
//            " ("
//        } else "",
//        postfix = if (markedEntries.size > 1) ")" else "",
//        separator = "; ",
//    ) { model ->
//
//        append(PATTERN.format(model.entry.y))
//    }
//}
//
//public object CustomMarkerLabelFormatter : MarkerLabelFormatter {
//
//    private val PATTERN = "%.02f"
//
//    override fun getLabel(
//        markedEntries: List<Marker.EntryModel>,
//        chartValues: ChartValues,
//    ) : CharSequence {
//        var sortedValues = arrayListOf<Float>()
//
//
//        for (i in 0 until markedEntries.size) {
//            sortedValues.add(markedEntries[i].entry.y)
//        }
//
//        sortedValues.sort()
//
//        val markerData = sortedValues.transformToSpannable(
//            prefix = if (markedEntries.size > 1) {
//                " ("
//            } else "",
//            postfix = if (markedEntries.size > 1) ")" else "",
//            separator = "; ",
//        ) { value ->
//
//            append(PATTERN.format(value))
//        }
//
//
//
//
//        return markerData
//    }
//}

