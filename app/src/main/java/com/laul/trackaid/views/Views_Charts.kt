package com.laul.trackaid

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.ModuleData
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp


/** Chart section in main view for each card module
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart(module: ModuleData){
    androidx.compose.material.Surface {
        Chart(
            autoScaleUp = AutoScaleUp.Full ,
            chart = columnChart(
                columns = listOf(
                    lineComponent(
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        thickness = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                    ),
                    lineComponent(
                        thickness = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        color = androidx.compose.ui.graphics.Color(module.mColor_Primary!!)
                    ),
                ),
                spacing = 9.dp,
/*
                dataLabel = com.patrykandpatrick.vico.core.component.text.textComponent(),
*/
                mergeMode = ColumnChart.MergeMode.Stack,
            ),
            model = module.cChartModel,

        )
    }
}


/** Chart section in detailed views
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart_Detailed(module: ModuleData){
}