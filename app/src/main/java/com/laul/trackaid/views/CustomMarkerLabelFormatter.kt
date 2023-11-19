package com.laul.trackaid.views

import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.extension.appendCompat
import com.patrykandpatrick.vico.core.extension.sumOf
import com.patrykandpatrick.vico.core.extension.transformToSpannable
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter

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

public object CustomMarkerLabelFormatter : MarkerLabelFormatter {

    private val PATTERN = "%.02f"

    override fun getLabel(
        markedEntries: List<Marker.EntryModel>,
        chartValues: ChartValues,
    ) : CharSequence {
        var sortedValues = arrayListOf<Float>()


        for (i in 0 until markedEntries.size) {
            sortedValues.add(markedEntries[i].entry.y)
        }

        sortedValues.sort()

        val markerData = sortedValues.transformToSpannable(
            prefix = if (markedEntries.size > 1) {
                " ("
            } else "",
            postfix = if (markedEntries.size > 1) ")" else "",
            separator = "; ",
        ) { value ->

            append(PATTERN.format(value))
        }




        return markerData
    }
}

