package com.laul.trackaid

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.lineSpec
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.AxisRenderer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel


private const val MAX_LABEL_COUNT = 6
private val bottomAxisItemPlacer = AxisItemPlacer.Horizontal.default(20, 0, false ,true )
private val bottomAxisItemPlacer_Detailed = AxisItemPlacer.Horizontal.default(4, 0, false ,true )
private val horizontalLayout = HorizontalLayout.FullWidth(
    scalableStartPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
    scalableEndPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
)



@Composable
private fun getColumns(module:ModuleData): List<LineComponent>{
    var columns = arrayListOf<LineComponent>()


    for (i in 0 until module.nCol) {
        if (module.nCol == 1 || i % 2 == 1) {
            columns.add(
                rememberLineComponent(
                    thickness = 6.dp,
                    shape = RoundedCornerShape(2.dp),
                    color = Color(module.mColor_Primary!!),

                )
            )
        } else {
            columns.add(
                rememberLineComponent(
                    color = Color.Transparent,
                    thickness = 6.dp,
                    shape = RoundedCornerShape(2.dp),

                )
            )
        }
    }

    return columns
}

/** LineChart creation and display set-up
 * @param module: module from DataProvider data class
 * @param type: String: set "Line" to display a curve + vertical Gradient
 * @param targetVerticalAxisPosition
 */
@Composable
fun getLines( module: ModuleData, type:String): List<LineCartesianLayer.LineSpec> {
    var lines = listOf(
        lineSpec(
            shader = if (type == "Line") DynamicShaders.color(Color(module.mColor_Primary!!)) else DynamicShaders.color(Color.Transparent),
            thickness   = if (type == "Line") 1.dp else 0.dp,
            pointSize = if (type == "Line") 2.dp else 5.dp,
            point = rememberShapeComponent(pillShape),

        )
    )

    return lines
}


@Composable
fun customStartAxis(module: ModuleData): AxisRenderer<AxisPosition.Vertical.Start> {
    var startAxis : VerticalAxis<AxisPosition.Vertical.Start> = rememberStartAxis()

    startAxis = rememberStartAxis(
        guideline = null,
        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
        titleComponent = rememberTextComponent(
            padding = dimensionsOf(2.dp, 2.dp),
            margins = dimensionsOf(2.dp),
        ),
        axis = rememberLineComponent(color = Color.Black, thickness = 1.dp),
        tick = null,

        itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = { MAX_LABEL_COUNT }) },

        )

    return startAxis
}

@Composable
fun customBottomAxis(module: ModuleData, isDetailedView: Boolean): AxisRenderer<AxisPosition.Horizontal.Bottom> {
    var bottomAxis = rememberBottomAxis()
    if (isDetailedView) {
        bottomAxis = rememberBottomAxis(
            label = rememberTextComponent(),
            axis = rememberLineComponent(color = Color.Black, thickness = 1.dp),
            guideline = null,
            tick = null,
            valueFormatter = { x, _, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
            )
    }
    else {

        bottomAxis = rememberBottomAxis(
            label = rememberTextComponent(
                textSize = MaterialTheme.typography.labelSmall.fontSize,
                padding = MutableDimensions(0f, 5f,0f,0f)
            ),

            axis =  null,
            guideline = null,
            tick = null,
            valueFormatter = { x, _, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
            )
    }
    return bottomAxis
}
/** Chart section in main view for each card module
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart(
    module: ModuleData,
    isDetailedView: Boolean,
) {
     when (module.mName) {
            // STEPS - Total Count
            "Steps" -> {
                CartesianChartHost(
                    autoScaleUp = AutoScaleUp.Full,
                    chartScrollSpec =  rememberChartScrollSpec(false),
                    marker = rememberMarker(),
                    modifier = Modifier
                        .padding(bottom = 10.dp),

                    chart = rememberCartesianChart(

                        layers = arrayOf(
                            rememberColumnCartesianLayer(
                                spacing = 4.dp,
                                columns = getColumns(module),
                                mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                            )
                        ),
                        startAxis = if(isDetailedView) customStartAxis(module) else null,
                        bottomAxis = customBottomAxis(module, isDetailedView) ,

                        ),
                    model = CartesianChartModel(
                        ColumnCartesianLayerModel.build {
                            series(x = module.series_all.s_sumD.x, y = module.series_all.s_sumD.y)
                        }
                    )
                )
            }

            // GLUCOSE and HR - Min, Max, Avg
            "Glucose", "Heart Rate" -> {
                CartesianChartHost(
                    marker = rememberMarker(),
                    autoScaleUp = AutoScaleUp.Full,
                    chartScrollSpec =  rememberChartScrollSpec(false),



                    chart = rememberCartesianChart(
                        layers = arrayOf(
                            rememberColumnCartesianLayer(
                                spacing = 4.dp,
                                columns = getColumns(module),
                                mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                            ),

                            rememberLineCartesianLayer(
                                spacing = 4.dp,
                                lines = getLines(module, "Points")
                            )
                        ),
                        startAxis = if(isDetailedView) customStartAxis(module) else null,
                        bottomAxis = customBottomAxis(module, isDetailedView),
                    ),
                    model = CartesianChartModel(
                        ColumnCartesianLayerModel.build {
                            series(x = module.series_all.s_min.x, y = module.series_all.s_min.y)
                            series(x = module.series_all.s_max.x, y = module.series_all.s_max.y)
                        },
                        LineCartesianLayerModel.build {
                            series(x = module.series_all.s_avg.x, y = module.series_all.s_avg.y)
                        },
                    )
                )
            }
        }
    }
//}
//
//        if (module.chartType == "Columns") {
//            CartesianChartHost(
//                chart = rememberCartesianChart(
//                    rememberColumnCartesianLayer(),
//                ),
//                model = CartesianChartModel(
//                    ColumnCartesianLayerModel.build {
//                        series(x = module.series_all.s_min.x, y = module.series_all.s_min.y)
//                        series(x = module.series_all.s_max.x, y = module.series_all.s_max.y)
//                    },
//                    LineCartesianLayerModel.build {
//                        series(x = module.series_all.s_avg.x, y = module.series_all.s_avg.y)
//                    },
//                )
//            )
//        }

            // var cChartModel_DailyMinMax = entryModelOf(*module.cFloatEntries_DailyMinMax.toTypedArray())

//            Chart(
//                autoScaleUp = AutoScaleUp.Full,
//                marker = rememberMarker(),
//                modifier = Modifier.background(backgroundColor),
//                chart = getColumnChart(module = module),
//                model = cChartModel_DailyMinMax,
//                bottomAxis = if (isDetailedView) bottomAxis(
//                    guideline = null,
//                    //itemPlacer = bottomAxisItemPlacer,
//                    valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
//                    titleComponent = textComponent(
//                        padding = dimensionsOf(2.dp, 2.dp),
//                        margins = dimensionsOf(2.dp),
//
//                        )
//                ) else null,
//                startAxis = if (isDetailedView) startAxis(
//                    guideline = null,
//                    horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
//                    titleComponent = textComponent(
//                        padding = dimensionsOf(2.dp, 2.dp),
//                        margins = dimensionsOf(2.dp),
//                    ),
//
//                    maxLabelCount = MAX_LABEL_COUNT,
//                ) else null,
//
//            )
//        }
//        if (module.chartType == "Line") {
//            var cChartModel_DailyMinMax = entryModelOf(*module.cFloatEntries_DailyMinMax.toTypedArray())
//
//            Chart(
//                chartScrollState = rememberChartScrollState(),
//                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true),
//
//                chart = getLineChart(
//                    module = module,
//                    type = "Line"
//                ),
//                modifier = Modifier.background(backgroundColor),
//
//                marker = rememberMarker(),
//                model = cChartModel_DailyMinMax,
//                autoScaleUp = AutoScaleUp.Full,
//                bottomAxis = if (isDetailedView) bottomAxis(
//                    guideline = null,
//                    itemPlacer = bottomAxisItemPlacer,
//                    valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
//                    titleComponent = textComponent(
//                        padding = dimensionsOf(2.dp, 2.dp),
//                        margins = dimensionsOf(2.dp),
//                    )
//                ) else null,
//            )

//        }
//        if (module.chartType == "Combo") {
//            var model = CartesianChartModel
//            (arrayOf(
//                ColumnCartesianLayerModel.build {
//                    series(x = module.series_all.s_min.x, y = module.series_all.s_min.y)
//                series(x = module.series_all.s_max.x, y = module.series_all.s_max.y)
//                },
//                LineCartesianLayerModel.build {
//                    series(x = module.series_all.s_avg.x, y = module.series_all.s_avg.y)
//                }
//            ))
//            //            var cChartModel_DailyMinMax = entryModelOf(*module.cFloatEntries_DailyMinMax.toTypedArray())
////            var cChartModel_DailyAvg = entryModelOf(*module.cFloatEntries_DailyAvg.toTypedArray())
//            CartesianChartHost(
//                chart = rememberCartesianChart(
//                    layers = arrayOf(
//                        rememberColumnCartesianLayer(
//                            columns = columns,
//                            //                        axisValueOverrider = AxisValueOverrider.fixed(minY = -2f, maxY = 0f),
//                            mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
//                        ),
//
//                        rememberLineCartesianLayer()
//                    ),
//                    startAxis =
//                    rememberStartAxis(
//                        itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = { 3 }) },
//                    ),
//                    bottomAxis = rememberBottomAxis(),
////
////                    rememberLineCartesianLayer(
////                        listOf(
////                            lineSpec(
////                                point = null,
////                                shader = DynamicShaders.color(Color.Black),
////                                backgroundShader =
////                                DynamicShaders.fromComponent(
////                                    componentSize = 4.dp,
////                                    component = rememberShapeComponent(shape = Shapes.pillShape),
////                                ),
////                            ),
////                        ),
////                        axisValueOverrider = AxisValueOverrider.fixed(minX = 0f, maxY = 3f),
////                    ),
////                ),
//                ),
//                model = CartesianChartModel(
//                    ColumnCartesianLayerModel.build {
//                        series(x = module.series_all.s_min.x, y = module.series_all.s_min.y)
//                        series(x = module.series_all.s_max.x, y = module.series_all.s_max.y)
////
////                        series(x = module.series_all.s_min.x, y = module.series_all.s_min.y)
////                        series(x = module.series_all.s_max.x, y = module.series_all.s_max.y)
//                    },
//                    LineCartesianLayerModel.build {
//                        series(x = module.series_all.s_avg.x, y = module.series_all.s_avg.y)
//                    },
//
//                ),
//
////            Chart(
////                chartScrollState = rememberChartScrollState(),
////                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true),
////                chart = getLineChart(
////                    module = module,
////                    type = "Points"
////                ) + getColumnChart(module = module),
////
////                marker = rememberMarker(),
////                modifier = Modifier
////                    .background(backgroundColor)
////                    .fillMaxWidth(),
////                model = cChartModel_DailyAvg.plus(cChartModel_DailyMinMax),
////
////                startAxis = if (isDetailedView) rememberStartAxis(
////                    title = module.mUnit,
////                    guideline = null,
////                    itemPlacer = AxisItemPlacer.Vertical.default(
////                        module.stats!!.value.max.ceil.toInt()+  module.stats!!.value.max.ceil.toInt()%2+1 // Set minimum Y-axis value
////                    )
////
////                ) else null,
////
////
////                bottomAxis = if (isDetailedView) rememberBottomAxis(
////                    guideline = null,
////                    valueFormatter = { x, _ ->
////                        module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size]
////                    },
////                    titleComponent = textComponent(
////                        padding = dimensionsOf(2.dp, 2.dp),
////                        margins = dimensionsOf(2.dp),
////                    ),
////
////                    ) else null,
////
////            )
//            )


//
//
//    /** Chart section in detailed views
//     * @param module: module from DataProvider data class
//     */
//    @Composable
//    fun compChart_Detailed(module: ModuleData, backgroundColor: Color) {
//        var cChartModel_Records = entryModelOf(*module.cFloatEntries_Records.toTypedArray())
//
//        if (module.mName == "Steps") {
//            Chart(
//                chart = getColumnChart(module = module),
//                model = cChartModel_Records,
//                startAxis = rememberStartAxis(
//                    title = module.mUnit,
//                    guideline = null
//                ),
//
//                chartScrollSpec = rememberChartScrollSpec(
//                    isScrollEnabled = true,
//                    initialScroll = InitialScroll.End
//                ),
//                bottomAxis = bottomAxis(
//                    guideline = null,
//                    itemPlacer = bottomAxisItemPlacer_Detailed,
//                    valueFormatter = { x, _ -> module.bottomAxisValues_Detailed[x.toInt() % module.bottomAxisValues_Detailed.size] },
//                    tick =  LineComponent(color = Color.Transparent.toArgb(), thicknessDp = 0f)
//                )
//            )
//        }
//        else {
//            Chart(
//                chartScrollState = rememberChartScrollState(),
//                chart = getLineChart(
//                    module = module,
//                    type = "Line"
//                ),
//                modifier = Modifier.background(backgroundColor),
//                autoScaleUp = AutoScaleUp.Full,
//                horizontalLayout = horizontalLayout,
//                chartScrollSpec = rememberChartScrollSpec(
//                    isScrollEnabled = true,
//                    initialScroll = InitialScroll.End
//                ),
//                marker = rememberMarker(),
//                model = cChartModel_Records,
//
//                startAxis = rememberStartAxis(
//                    title = module.mUnit,
//                    guideline = null
//                ),
//
//                bottomAxis = bottomAxis(
//                    guideline = null,
//                    itemPlacer = bottomAxisItemPlacer,

//            valueFormatter = { x, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
//                    valueFormatter = { x, _
//                        ->
//                        getDate(x.toLong(), "HH:mm")
//                    },

//                    titleComponent = textComponent(
//                        padding = dimensionsOf(2.dp, 2.dp),
//                        margins = dimensionsOf(2.dp),
//                    )
//                )
//            )
//
//        }
//
//    }
//
//

//
//    }
//
////
//    @Composable
//    fun getColumnChart(
//        module: ModuleData,
//    ): ColumnChart {
//        var columns = arrayListOf<LineComponent>()
//
//        for (i in 0 until module.nCol) {
//            if (module.nCol == 1 || i % 2 == 1) {
//                columns.add(
//                    lineComponent(
//                        thickness = 8.dp,
//                        shape = RoundedCornerShape(4.dp),
//                        color = Color(module.mColor_Primary!!),
//                        dynamicShader = verticalGradient(
//                            arrayOf(
//                                Color(module.mColor_Primary!!),
//                                Color(module.mColor_Primary!!).copy(alpha = 0.3f),
//                                Color(module.mColor_Primary!!)
//                            )
//                        ),
//                    )
//                )
//            } else {
//                columns.add(
//
//                    lineComponent(
//                        color = Color.Transparent,
//                        thickness = 8.dp,
//                        shape = RoundedCornerShape(4.dp),
//                        dynamicShader = verticalGradient(
//                            arrayOf(
//                                Color(module.mColor_Primary!!),
//                                Color(module.mColor_Primary!!).copy(alpha = 0.1f)
//                            )
//
//                        ),
//                    )
//                )
//            }
//        }
//
//        return columnChart(
//            columns = columns,
//            spacing = 5.dp,
//            mergeMode = ColumnChart.MergeMode.Stack,
//            axisValuesOverrider = if (module.mName != "Steps") AxisValuesOverrider.fixed(
//                maxY = module.stats!!.value.max.ceil+  module.stats!!.value.max.ceil %2
//        )  else null
//        )
//
//    }
//
