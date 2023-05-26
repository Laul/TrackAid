package com.laul.trackaid

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.rememberMarker
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp


/** Chart section in main view for each card module
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart(module: ModuleData){
    androidx.compose.material.Surface {
        if (module.chartType == "Columns") {
            Chart(
                autoScaleUp = AutoScaleUp.Full,
                marker =rememberMarker(),

                chart = columnChart(
                    columns = listOf(
                        lineComponent(
                            color = androidx.compose.ui.graphics.Color.Transparent,
                            thickness = 8.dp,
                            shape = RoundedCornerShape(4.dp),
                            dynamicShader = verticalGradient(arrayOf(Color(module.mColor_Primary!!), Color(module.mColor_Primary!!).copy(alpha = 0f))),
                            ),
                        lineComponent(
                            thickness = 8.dp,
                            shape = RoundedCornerShape(4.dp),
                            color = Color(module.mColor_Primary!!),
                            dynamicShader = verticalGradient(arrayOf(Color(module.mColor_Primary!!), Color(module.mColor_Primary!!).copy(alpha = 0.7f), Color(module.mColor_Primary!!))),

                        ),
                    ),
                    spacing = 9.dp,
/*
                dataLabel = com.patrykandpatrick.vico.core.component.text.textComponent(),
*/
                    mergeMode = ColumnChart.MergeMode.Stack,
                ),
                model = module.cChartModel_Columns,

            )
        }
        if (module.chartType == "Line") {
            Chart(
                chart = getLineChart(
                    module = module,

                ),
                marker =rememberMarker(),
                model = module.cChartModel_Columns,
                autoScaleUp = AutoScaleUp.Full


            )

        }
    }
}


/** Chart section in detailed views
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart_Detailed(module: ModuleData){
}


@Composable
fun getLineChart(
    module: ModuleData,
    targetVerticalAxisPosition: AxisPosition.Vertical? = null,
): LineChart = com.patrykandpatrick.vico.compose.chart.line.lineChart(
    lines = listOf(
        com.patrykandpatrick.vico.compose.chart.line.lineSpec(
            lineColor = Color(module.mColor_Primary!!),
            lineBackgroundShader = verticalGradient(
                arrayOf(Color(module.mColor_Primary!!), Color(module.mColor_Primary!!).copy(alpha = 0f)),
            ),
        ),
    ),
    spacing = 5.dp,
    targetVerticalAxisPosition = targetVerticalAxisPosition,
)


@Composable
fun getColumnChart(
    module: ModuleData,
    targetVerticalAxisPosition: AxisPosition.Vertical? = null,
): ColumnChart = columnChart(
    columns = listOf(

        lineComponent(
            color = androidx.compose.ui.graphics.Color.Transparent,
            thickness = 8.dp,
            shape = RoundedCornerShape(4.dp),
            dynamicShader = verticalGradient(arrayOf(Color(module.mColor_Primary!!), Color(module.mColor_Primary!!).copy(alpha = 0f))),
        ),


    ),
    targetVerticalAxisPosition = targetVerticalAxisPosition,
)

