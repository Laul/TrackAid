package com.laul.trackaid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.entry.plus
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.extension.half
import com.patrykandpatrick.vico.core.scroll.InitialScroll

private const val BOTTOM_AXIS_ITEM_SPACING = 100
private const val BOTTOM_AXIS_ITEM_OFFSET = 8
private const val MIN_VALUE = 8
private const val MAX_LABEL_COUNT = 6
private val bottomAxisItemPlacer = AxisItemPlacer.Horizontal.default(BOTTOM_AXIS_ITEM_SPACING, BOTTOM_AXIS_ITEM_OFFSET, true)

private val horizontalLayout = HorizontalLayout.FullWidth(
    startPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
    endPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
)
private const val MAX_LABELS = 2
private const val START_AXIS_ITEM_OFFSET = 8
private val startAxisItemPlacer = AxisItemPlacer.Vertical.default(MAX_LABELS, true)

/** Chart section in main view for each card module
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart(
    module: ModuleData,
    isDetailedView: Boolean,
    backgroundColor: Color
) {
    androidx.compose.material.Surface {
        if (module.chartType == "Columns") {
            Chart(
                autoScaleUp = AutoScaleUp.Full,
                marker = rememberMarker(),
                modifier = Modifier.background(backgroundColor),
                chart = getColumnChart(module = module, isDetailedView),
                model = module.cChartModel_DailyMinMax,
                bottomAxis = if (isDetailedView) bottomAxis(
                    guideline = null,
                    itemPlacer = bottomAxisItemPlacer,
                    valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
                    titleComponent = textComponent(
                        padding = dimensionsOf(2.dp, 2.dp),
                        margins = dimensionsOf(2.dp),

                    )
                ) else null,
                startAxis =  if (isDetailedView) startAxis(
                    guideline = null,
                    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                    titleComponent = textComponent(
                        padding = dimensionsOf(2.dp, 2.dp),
                        margins = dimensionsOf(2.dp),
                    ),

                    maxLabelCount = MAX_LABEL_COUNT,
                    title = module.mUnit,
                ) else null,

            )
        }
        if (module.chartType == "Line") {
            Chart(
                chartScrollState = rememberChartScrollState(),
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true),

                chart = getLineChart(
                    module = module,
                    type = "Line"
                ),
                modifier = Modifier.background(backgroundColor),

                marker = rememberMarker(),
                model = module.cChartModel_DailyMinMax,
                autoScaleUp = AutoScaleUp.Full,
                bottomAxis = if (isDetailedView) bottomAxis(
                    guideline = null,
                    itemPlacer = bottomAxisItemPlacer,
                    valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
                    titleComponent = textComponent(
                        padding = dimensionsOf(2.dp, 2.dp),
                        margins = dimensionsOf(2.dp),
                    )
                ) else null,
            )

        }
        if (module.chartType == "Combo") {
            Chart(
                chartScrollState = rememberChartScrollState(),
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true),
                chart = getLineChart(
                    module = module,
                    type = "Points"
                ) + getColumnChart(module = module, isDetailedView),

                marker = rememberMarker(),
                modifier = Modifier
                    .background(backgroundColor)
                    .fillMaxWidth(),
                model = module.cChartModel_DailyAvg + module.cChartModel_DailyMinMax,
                autoScaleUp = AutoScaleUp.Full,

                startAxis = if (isDetailedView) rememberStartAxis(
                    title = module.mUnit,
                    guideline = null

                    ) else null,


                bottomAxis = if (isDetailedView) rememberBottomAxis(
                    guideline = null,
                    valueFormatter = { x, _ ->
                        module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
                    titleComponent = textComponent(
                        padding = dimensionsOf(2.dp, 2.dp),
                        margins = dimensionsOf(2.dp),
                    ),

                ) else null,



//
//                if (isStartAxis) startAxis(
//                    guideline = null,
//                    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
//                    titleComponent = textComponent(
//                        padding = dimensionsOf(2.dp, 2.dp),
//                        margins = dimensionsOf(2.dp),
//                    ),
//                    itemPlacer = startAxisItemPlacer,
//                )
//                else null,
//
            )

        }
    }
}


/** Chart section in detailed views
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart_Detailed(module: ModuleData, backgroundColor: Color) {
    Chart(
//        chartScrollState = rememberChartScrollState(),
//        chartScrollSpec = rememberChartScrollSpec( initialScroll = InitialScroll.End),
        chart = getLineChart(
            module = module,
            type = "Line"
        ),
        modifier = Modifier.background(backgroundColor),
        autoScaleUp = AutoScaleUp.Full,
        horizontalLayout = horizontalLayout,

        marker = rememberMarker(),
        model = module.cChartModel_Records,
        bottomAxis =  bottomAxis(
            guideline = null,
            itemPlacer = bottomAxisItemPlacer,
            valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
            titleComponent = textComponent(
                padding = dimensionsOf(2.dp, 2.dp),
                margins = dimensionsOf(2.dp),
            )
        )
    )

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
            lineColor = if (type == "Line") Color(module.mColor_Primary!!) else Color.Transparent,
            lineThickness = if (type == "Line") 2.dp else 0.dp,
            pointSize = if (type == "Line") 5.dp else 7.dp,
            point = shapeComponent(shape = Shapes.pillShape),
            lineBackgroundShader =
            if (type == "Line")
                verticalGradient(
                    arrayOf(
                        Color(module.mColor_Primary!!),
                        Color(module.mColor_Primary!!).copy(alpha = 0f)
                    )
                )
            else
                null,
        ),
    ),
    spacing = 1.dp
)


@Composable
fun getColumnChart(
    module: ModuleData,
    isBottomAxis: Boolean
): ColumnChart {
    var columns = arrayListOf<LineComponent>()

    for (i in 0 until module.nCol) {
        if (module.nCol== 1 || i % 2 == 1) {
            columns.add(
                lineComponent(
                    thickness = 8.dp,
                    shape = RoundedCornerShape(4.dp),
                    color = Color(module.mColor_Primary!!),
                    dynamicShader = verticalGradient(
                        arrayOf(
                            Color(module.mColor_Primary!!),
                            Color(module.mColor_Primary!!).copy(alpha = 0.3f),
                            Color(module.mColor_Primary!!)
                        )
                    ),
                )
            )
        } else {
            columns.add(

                lineComponent(
                    color = Color.Transparent,
                    thickness = 8.dp,
                    shape = RoundedCornerShape(4.dp),
                    dynamicShader = verticalGradient(
                        arrayOf(
                            Color(module.mColor_Primary!!),
                            Color(module.mColor_Primary!!).copy(alpha = 0.1f)
                        )

                    ),
                )
            )
        }
    }

    return columnChart(
        columns = columns,
        spacing = 5.dp,
        mergeMode = ColumnChart.MergeMode.Stack,
    )

}
