package com.laul.trackaid.views

import android.text.Spannable
import android.text.style.ForegroundColorSpan
import com.patrykandpatrick.vico.core.extension.appendCompat
import com.patrykandpatrick.vico.core.extension.sumOf
import com.patrykandpatrick.vico.core.extension.transformToSpannable
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
//
//public object CustomMarkerLabelFormatter : MarkerLabelFormatter {
//
//    private const val PATTERN = "%.02f"
//
//    override fun getLabel(
//        markedEntries: List<Marker.EntryModel>,
//    ): CharSequence = markedEntries.transformToSpannable(
//        prefix = if (markedEntries.size > 1) {
//            PATTERN.format(markedEntries.sumOf { it.entry.y }) + " ("
//        } else "",
//        postfix = if (markedEntries.size > 1) ")" else "",
//        separator = "; ",
//    ) { model ->
//        appendCompat(
//            PATTERN.format(model.entry.y),
//            ForegroundColorSpan(model.color),
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
//        )
//    }
//}
//
