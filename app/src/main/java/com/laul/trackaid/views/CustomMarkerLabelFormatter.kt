package com.laul.trackaid.views

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.scale
import com.laul.trackaid.data.ModuleData
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import com.patrykandpatrick.vico.core.model.CartesianLayerModel
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel




public class CustomMarkerLabelFormatter(module: ModuleData): MarkerLabelFormatter {

    val module = module

    override fun getLabel(
        markedEntries: List<Marker.EntryModel>,
        chartValues: ChartValues,
    ): CharSequence {

        if (markedEntries.size == 3){

            var label = SpannableStringBuilder()

            .bold { color(module.mColor_Primary!! , { append("%.02f".format(markedEntries[2].entry.y )) }) }

            .append(System.lineSeparator())
            .scale(0.75f, { append( "%.02f".format(markedEntries[0].entry.y) + " - "+ "%.02f".format(markedEntries[1].entry.y+markedEntries[0].entry.y))})

            return label
        }




        return markedEntries.transformToSpannable(
            prefix = if (markedEntries.size > 1) {
                " ("
            } else "",
            postfix = if (markedEntries.size > 1) ")" else "",
            separator = "; ",
        ) { model ->
            append(
                "%.02f".format(model.entry.y),
                ForegroundColorSpan(model.color),
                Spannable.SPAN_COMPOSING,
            )

//        append(PATTERN.format(model.entry.y))
        }
    }


    private val CartesianLayerModel.Entry.y
        get() =
            when (this) {
                is ColumnCartesianLayerModel.Entry -> y
                is LineCartesianLayerModel.Entry -> y
                else -> throw IllegalArgumentException("Unexpected `CartesianLayerModel.Entry` implementation.")
            }

}

internal fun <T> Iterable<T>.transformToSpannable(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "…",
    transform: SpannableStringBuilder.(T) -> Unit,
): Spannable {
    val buffer = SpannableStringBuilder()
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) buffer.transform(element) else break
    }
    if (limit in 0..<count) buffer.append(truncated)
    buffer.append(postfix)
    return buffer
}
