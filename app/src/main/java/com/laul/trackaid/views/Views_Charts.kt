package com.laul.trackaid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.DataGeneral.Companion.getDate
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.composed.ComposedChartEntryModel
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.extension.ceil
import com.patrykandpatrick.vico.core.extension.floor
import com.patrykandpatrick.vico.core.extension.half
import com.patrykandpatrick.vico.core.extension.piRad
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

private const val BOTTOM_AXIS_ITEM_SPACING = 15
private const val BOTTOM_AXIS_ITEM_OFFSET = 0
private const val MIN_VALUE = 8
private const val MAX_LABEL_COUNT = 6
private val bottomAxisItemPlacer =
    AxisItemPlacer.Horizontal.default(BOTTOM_AXIS_ITEM_SPACING, BOTTOM_AXIS_ITEM_OFFSET, false ,true )

private val horizontalLayout = HorizontalLayout.FullWidth(
    scalableStartPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
    scalableEndPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
)

/** Chart section in main view for each card module
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart(
    module: ModuleData,
    isDetailedView: Boolean,
    backgroundColor: Color
) {



    androidx.compose.material.Surface (
        modifier = Modifier.height(250.dp)
    ) {
        if (module.chartType == "Columns") {
            var cChartModel_DailyMinMax = entryModelOf(*module.cFloatEntries_DailyMinMax.toTypedArray())

            Chart(
                autoScaleUp = AutoScaleUp.Full,
                marker = rememberMarker(),
                modifier = Modifier.background(backgroundColor),
                chart = getColumnChart(module = module),
                model = cChartModel_DailyMinMax,
                bottomAxis = if (isDetailedView) bottomAxis(
                    guideline = null,
                    itemPlacer = bottomAxisItemPlacer,
                    valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
                    titleComponent = textComponent(
                        padding = dimensionsOf(2.dp, 2.dp),
                        margins = dimensionsOf(2.dp),

                        )
                ) else null,
                startAxis = if (isDetailedView) startAxis(
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
            var cChartModel_DailyMinMax = entryModelOf(*module.cFloatEntries_DailyMinMax.toTypedArray())

            Chart(
                chartScrollState = rememberChartScrollState(),
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true),

                chart = getLineChart(
                    module = module,
                    type = "Line"
                ),
                modifier = Modifier.background(backgroundColor),

                marker = rememberMarker(),
                model = cChartModel_DailyMinMax,
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
            var cChartModel_DailyMinMax = entryModelOf(*module.cFloatEntries_DailyMinMax.toTypedArray())
            var cChartModel_DailyAvg = entryModelOf(*module.cFloatEntries_DailyAvg.toTypedArray())

            Chart(
                chartScrollState = rememberChartScrollState(),
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true),
                chart = getLineChart(
                    module = module,
                    type = "Points"
                ) + getColumnChart(module = module),

                marker = rememberMarker(),
                modifier = Modifier
                    .background(backgroundColor)
                    .fillMaxWidth(),
                model = cChartModel_DailyAvg.plus(cChartModel_DailyMinMax),

                startAxis = if (isDetailedView) rememberStartAxis(
                    title = module.mUnit,
                    guideline = null,
                    itemPlacer = AxisItemPlacer.Vertical.default(
                        module.stats!!.value.max.ceil.toInt()+  module.stats!!.value.max.ceil.toInt()%2+1 // Set minimum Y-axis value
                    )

                ) else null,


                bottomAxis = if (isDetailedView) rememberBottomAxis(
                    guideline = null,
                    valueFormatter = { x, _ ->
                        module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size]
                    },
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
        var cChartModel_Records = entryModelOf(*module.cFloatEntries_Records.toTypedArray())

        Chart(
            chartScrollState = rememberChartScrollState(),
//        chartScrollSpec = rememberChartScrollSpec( initialScroll = InitialScroll.End),
            chart = getLineChart(
                module = module,
                type = "Line"
            ),
            modifier = Modifier.background(backgroundColor),
            autoScaleUp = AutoScaleUp.Full,
            horizontalLayout = horizontalLayout,
            chartScrollSpec = rememberChartScrollSpec(
                isScrollEnabled = true,
                initialScroll = InitialScroll.End
            ),
            marker = rememberMarker(),
            model = cChartModel_Records,

            startAxis = rememberStartAxis(
                title = module.mUnit,
                guideline = null
            ),

            bottomAxis = bottomAxis(
                guideline = null,
                itemPlacer = bottomAxisItemPlacer,

//            valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
                valueFormatter = { x, _
                    -> getDate(x.toLong(),"HH:mm" )
                },

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
    ): LineChart {
        val marker = rememberMarker()
        return lineChart(
            persistentMarkers = remember(marker) { mapOf(1f to marker) },
            lines = listOf(
                com.patrykandpatrick.vico.compose.chart.line.lineSpec(
                    lineColor = if (type == "Line") Color(module.mColor_Primary!!) else Color.Transparent,
                    lineThickness = if (type == "Line") 1.dp else 0.dp,
                    pointSize = if (type == "Line") 2.dp else 5.dp,
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

    }


    @Composable
    fun getColumnChart(
        module: ModuleData,
    ): ColumnChart {
        var columns = arrayListOf<LineComponent>()

        for (i in 0 until module.nCol) {
            if (module.nCol == 1 || i % 2 == 1) {
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
            axisValuesOverrider = AxisValuesOverrider.fixed(
                maxY = module.stats!!.value.max.ceil+  module.stats!!.value.max.ceil %2 // Adjust Y-axis value for even numbers
            )
        )

    }



