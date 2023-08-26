package com.laul.trackaid

import android.graphics.Typeface
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.entry.plus
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.Component
import com.patrykandpatrick.vico.core.component.shape.Shapes
import kotlinx.coroutines.supervisorScope


/** Chart section in main view for each card module
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart(
    module: ModuleData,
    isBottomAxis: Boolean
){
    androidx.compose.material.Surface {
        if (module.chartType == "Columns") {
            Chart(
                autoScaleUp = AutoScaleUp.Full,
                marker =rememberMarker(),

                chart = getColumnChart(module = module, isBottomAxis),
                model = module.cChartModel_Columns,

            )
        }
        if (module.chartType == "Line") {
            Chart(
                chart = getLineChart(
                    module = module,
                    type = "Line"
                    ),
                marker =rememberMarker(),
                model = module.cChartModel_Columns,
                autoScaleUp = AutoScaleUp.Full

            )

        }
        if (module.chartType == "Combo") {
            Chart(
                chart = getLineChart(module = module, type = "Points") + getColumnChart(module =module, isBottomAxis),
                marker =rememberMarker(),
                model = module.cChartModel_Lines + module.cChartModel_Columns ,
                autoScaleUp = AutoScaleUp.Full,

//                startAxis = startAxis(
//                    guideline = null,
//                    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
//                    titleComponent = textComponent(
//                        background = shapeComponent(Shapes.pillShape, Color.Red),
//                        padding = dimensionsOf(2.dp, 2.dp),
//                        margins = dimensionsOf(2.dp),
//                    ),
//                    title = module.mUnit,
//                ),
                bottomAxis = if (isBottomAxis) bottomAxis(
                    guideline = null,
                    valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
                    titleComponent = textComponent(
                        padding = dimensionsOf(2.dp, 2.dp),
                        margins = dimensionsOf(2.dp),
                    )
                ) else null,
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




/** LineChart creation and display set-up
 * @param module: module from DataProvider data class
 * @param type: String: set "Line" to display a curve + vertical Gradient
 * @param targetVerticalAxisPosition
 */
@Composable
fun getLineChart(
    module: ModuleData,
    type: String,
): LineChart = com.patrykandpatrick.vico.compose.chart.line.lineChart(
    lines = listOf(
        com.patrykandpatrick.vico.compose.chart.line.lineSpec(
            lineColor = if (type == "Line" ) Color(module.mColor_Primary!!) else Color.Transparent,
            lineThickness = if (type == "Line" ) 2.dp else 0.dp,
            pointSize = if (type == "Line" ) 0.dp else 7.dp,
            point = shapeComponent(shape = Shapes.pillShape),
            lineBackgroundShader =
                if (type == "Line" )
                    verticalGradient( arrayOf(Color(module.mColor_Primary!!), Color(module.mColor_Primary!!).copy(alpha = 0f)))
                else
                    null,
        ),
    ),
    spacing = 5.dp
)


@Composable
fun getColumnChart(
    module: ModuleData,
    isBottomAxis : Boolean
): ColumnChart = columnChart(
    columns = listOf(
        lineComponent(
            color =Color.Transparent,
            thickness = 8.dp,
            shape = RoundedCornerShape(4.dp),
            dynamicShader = verticalGradient(arrayOf(Color(module.mColor_Primary!!), Color(module.mColor_Primary!!).copy(alpha = 0.1f))),
        ),
        lineComponent(
            thickness = 8.dp,
            shape = RoundedCornerShape(4.dp),
            color = Color(module.mColor_Primary!!),
            dynamicShader = verticalGradient(arrayOf(Color(module.mColor_Primary!!), Color(module.mColor_Primary!!).copy(alpha = 0.3f), Color(module.mColor_Primary!!))),

            ),

    ),
    spacing = 9.dp,
    mergeMode = ColumnChart.MergeMode.Stack,
)

