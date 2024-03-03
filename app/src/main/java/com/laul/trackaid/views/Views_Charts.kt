package com.laul.trackaid

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.theme.color_valid
import com.laul.trackaid.theme.md_theme_light_primaryContainer
import com.laul.trackaid.views.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
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
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.decoration.Decoration
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes.pillShape
import com.patrykandpatrick.vico.core.component.shape.shader.ColorShader
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import java.text.SimpleDateFormat
import kotlin.math.ceil
import kotlin.math.round


private val bottomAxisItemPlacer = AxisItemPlacer.Horizontal.default(20, 0, false, true)
private val bottomAxisItemPlacer_Detailed = AxisItemPlacer.Horizontal.default(4, 0, false, true)
private val horizontalLayout = HorizontalLayout.FullWidth(
    scalableStartPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
    scalableEndPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
)

private fun valFormat(value : Float): String {

    return SimpleDateFormat("E").format(value) + " - "  + SimpleDateFormat("hh:mm a").format(value)
}

@Composable
private fun getColumns(module: ModuleData, isDaily: Boolean): List<LineComponent> {
    var columns = arrayListOf<LineComponent>()


    for (i in 0 until module.nCol) {
        if (module.nCol == 1 || i % 2 == 1) {
            columns.add(
                rememberLineComponent(
                    thickness = if (isDaily != true) 5.dp else 10.dp,
                    shape = RoundedCornerShape(2.dp),
                    color = Color(module.mColor_Primary!!),

                    )
            )
        } else {
            columns.add(
                rememberLineComponent(
                    color = Color.Transparent,
                    thickness = 5.dp,
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
fun getLines(module: ModuleData, type: String): List<LineCartesianLayer.LineSpec> {
    var lines = listOf(
        lineSpec(
            shader = if (type == "Line") DynamicShaders.color(Color(module.mColor_Primary!!)) else ColorShader(
                Color.Transparent.toArgb()
            ),
            backgroundShader = ColorShader(Color.Transparent.toArgb()),
            thickness = if (type == "Line") 2.dp else 0.dp,
            pointSize = if (type == "Line") 0.dp else 4.dp,
            point = rememberShapeComponent(
                shape = pillShape,
                color = Color.White
            ),

            pointConnector = DefaultPointConnector(cubicStrength = 0f),

            )
    )

    return lines
}


@Composable
fun customStartAxis(module: ModuleData): Axis<AxisPosition.Vertical.Start> {
    var startAxis: VerticalAxis<AxisPosition.Vertical.Start> = rememberStartAxis()

    startAxis = rememberStartAxis(
        guideline = rememberLineComponent(
            color = md_theme_light_primaryContainer,
            thickness = 1.dp
        ),
        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
        titleComponent = rememberTextComponent(
            padding = dimensionsOf(2.dp, 2.dp),
            margins = dimensionsOf(2.dp),
        ),
        axis = null,
        tick = null,
        itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = { 5 }) },
        valueFormatter = { y, _, _ ->
            //module.startAxisValues[y.toInt()% module.startAxisValues.size] .toString()

            ceil(y).toInt().toString()
                         },

        )

    return startAxis
}

@Composable
fun customBottomAxis(
    module: ModuleData,
    isDetailedView: Boolean
): Axis<AxisPosition.Horizontal.Bottom> {
    var bottomAxis = rememberBottomAxis()
    if (isDetailedView) {
        bottomAxis = rememberBottomAxis(
            label = rememberTextComponent(),
            axis = null,
            guideline = null,
            tick = null,
            valueFormatter = { x, _, _ -> module.bottomAxisValues[x.toInt() % module.bottomAxisValues.size] },
        )
    } else {

        bottomAxis = rememberBottomAxis(
            label = rememberTextComponent(
                textSize = MaterialTheme.typography.labelSmall.fontSize,
                padding = MutableDimensions(0f, 5f, 0f, 0f)
            ),

            axis = null,
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
    var marker = rememberMarker(module)
    when (module.mName) {
        // STEPS - Total Count
        "Steps" -> {
            CartesianChartHost(

                autoScaleUp = AutoScaleUp.Full,
                chartScrollSpec = rememberChartScrollSpec(false),
                marker = if (isDetailedView) marker else null,
                modifier = Modifier
                    .padding(bottom = 10.dp),

                chart = rememberCartesianChart(

                    layers = arrayOf(
                        rememberColumnCartesianLayer(
                            spacing = 4.dp,
                            columns = getColumns(
                                module = module,
                                isDaily = false
                            ),
                            mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },

                            )
                    ),
                    startAxis = if (isDetailedView) customStartAxis(module) else null,
                    bottomAxis = customBottomAxis(module, isDetailedView),
                    persistentMarkers = if (isDetailedView) {
                        remember(marker) { mapOf(module.series_all.s_sumD.x.last() to marker) }
                    } else null,
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
                marker = if (isDetailedView) rememberMarker(module) else null,
                autoScaleUp = AutoScaleUp.Full,
                chartScrollSpec = rememberChartScrollSpec(false),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 10.dp),

                chart = rememberCartesianChart(
                    layers = arrayOf(
                        rememberColumnCartesianLayer(

                            spacing = 4.dp,
                            columns = getColumns(
                                module = module,
                                isDaily = false
                            ),
                            mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                            axisValueOverrider = if(isDetailedView) {
                                AxisValueOverrider.fixed(
                                    minY = round(module.stats!!.value.min!!* .5f),
                                    maxY = module.stats!!.value.max!!* 1.2f,
                                )
                            } else null,

//                            axisValueOverrider = AxisValueOverrider.fixed(
//                                maxY = if (ceil(module.stats!!.value.max) % 2 == 1f) {
//                                    ceil(module.stats!!.value.max) + 10
//                                } else ceil(module.stats!!.value.max),
//                            )
                        ),

                        rememberLineCartesianLayer(
                            axisValueOverrider = if(isDetailedView) {
                                AxisValueOverrider.fixed(
                                    minY = round( module.stats!!.value.min!!* .5f),
                                    maxY = module.stats!!.value.max!!* 1.2f,
                                )
                            } else null,
                            spacing = 4.dp,
                            lines = getLines(module, "Points")

                        )
                    ),
                    startAxis = if (isDetailedView) customStartAxis(module) else null,
                    bottomAxis = customBottomAxis(module, isDetailedView),
                    persistentMarkers = if (isDetailedView) {
                        remember(marker) { mapOf(module.series_all.s_sumD.x.last() to marker) }
                    } else null,

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

fun <K, V> Map<K, V?>.lastKeyOfNonZeroValue(): K? {
    return entries.reversed().firstOrNull {
        it.value != 0 }?.key
}

/** Chart section in detailed views
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart_Detailed(module: ModuleData) {
    var marker = rememberMarker(module)


    when (module.mName) {

        // STEPS - Sum per hour
        "Steps" -> {
            CartesianChartHost(

                autoScaleUp = AutoScaleUp.None,
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true, initialScroll= InitialScroll.End),
                marker = marker,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 10.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                ,

                chart = rememberCartesianChart(
                    persistentMarkers = remember(marker) { mapOf(module.series_all.s_sumH.x[module.series_all.s_sumH.y.reversed().mapIndexedNotNull { i, n -> i.takeIf { n != 0f } } [0]]to marker) },

                    layers = arrayOf(
                        rememberColumnCartesianLayer(
                            spacing = 12.dp,
                            columns = getColumns(
                                module = module,
                                isDaily = true
                            ),
                        )
                    ),
                    endAxis = rememberEndAxis(
                        guideline = rememberLineComponent(
                            color = md_theme_light_primaryContainer,
                            thickness = 1.dp
                        ),
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,

                        axis = null,
                        tick = null,
                        itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = { 5 }) },
                        valueFormatter = { y, _, _ -> y.toInt().toString() },


                        ) ,
                    bottomAxis = rememberBottomAxis(
                        guideline = null,
                        tick = null,
                        itemPlacer = remember { AxisItemPlacer.Horizontal.default(spacing = 4) },
                        valueFormatter = { x, _, _ -> module.bottomAxisValues_Detailed[x.toInt() % module.bottomAxisValues_Detailed.size] },
                        ),

                    ),
                model = CartesianChartModel(
                    ColumnCartesianLayerModel.build {
                        series(x = module.series_all.s_sumH.x, y = module.series_all.s_sumH.y)
                    }
                )
            )
        }

        // GLUCOSE and HR - All Data
        "Glucose", "Heart Rate" -> {
            CartesianChartHost(
                marker = marker,
                autoScaleUp = AutoScaleUp.None,
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true, initialScroll= InitialScroll.End),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 10.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                ,

                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        spacing = 4.dp,
                        lines = getLines(module, "Line"),
                        axisValueOverrider = AxisValueOverrider.adaptiveYValues(yFraction = 1.2f, round = true),


                    ),

                    persistentMarkers = remember(marker) { mapOf(module.series_all.s_all.x.last() to marker) },

                    endAxis = rememberEndAxis(
                            guideline = rememberLineComponent(
                                color = md_theme_light_primaryContainer,
                                thickness = 1.dp
                            ),
                            horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,

                            axis = null,
                            tick = null,
                            itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = { 5 }, shiftTopLines = true)  },
                            valueFormatter = { y, _, _ -> y.toInt().toString() },

                    ) ,
                    bottomAxis = rememberBottomAxis(
                        guideline = null,
                        tick = null,
                        itemPlacer = remember { AxisItemPlacer.Horizontal.default(spacing = 20) },
                        valueFormatter = { x, _, _ ->
                            valFormat(x) },

                        ),
                    decorations = createThresholdLines(module.target)
                ),
                model = CartesianChartModel(
                    LineCartesianLayerModel.build {
                        series(x = module.series_all.s_all.x, y = module.series_all.s_all.y)
                    },
                )

            )
        }

    }
}


/** Threshold lines creation.
 * @param targetValues: array list of threshold y values
 */
@Composable
fun createThresholdLines( targetValues: ArrayList<Float>) : List<Decoration>{
    var decoration = ArrayList<Decoration>()

    targetValues.forEach{
        decoration.add(
            ThresholdLine(
                thresholdValue = it,
                lineComponent = rememberShapeComponent(
                    strokeWidth = .5.dp,
                    color = color_valid.copy()
                ),
                labelComponent =
                    rememberTextComponent(
                        color = Color.Black,
                        padding = dimensionsOf(horizontal = 8.dp)
                    ),
                labelHorizontalPosition =  ThresholdLine.LabelHorizontalPosition.End,
                labelVerticalPosition =  ThresholdLine.LabelVerticalPosition.Bottom,

            ),
        )
    }

    if (targetValues.size ==2) {
        decoration.add(
            ThresholdLine(
                thresholdRange = targetValues[0]..targetValues[1],
                lineComponent = rememberShapeComponent(color = color_valid.copy(alpha = 0.03f)),
                thresholdLabel = "",
            ),


        )
    }

    return decoration
}