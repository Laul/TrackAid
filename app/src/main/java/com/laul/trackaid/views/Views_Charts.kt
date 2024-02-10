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
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.AxisRenderer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
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


private val bottomAxisItemPlacer = AxisItemPlacer.Horizontal.default(20, 0, false, true)
private val bottomAxisItemPlacer_Detailed = AxisItemPlacer.Horizontal.default(4, 0, false, true)
private val horizontalLayout = HorizontalLayout.FullWidth(
    scalableStartPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
    scalableEndPaddingDp = DefaultDimens.COLUMN_OUTSIDE_SPACING,
)


@Composable
private fun getColumns(module: ModuleData): List<LineComponent> {
    var columns = arrayListOf<LineComponent>()


    for (i in 0 until module.nCol) {
        if (module.nCol == 1 || i % 2 == 1) {
            columns.add(
                rememberLineComponent(
                    thickness = 5.dp,
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
            thickness = if (type == "Line") 1.dp else 0.dp,
            pointSize = if (type == "Line")0.dp else 4.dp,
            point = rememberShapeComponent(
                shape = pillShape,
                color = Color.White
            ),

            )
    )

    return lines
}


@Composable
fun customStartAxis(module: ModuleData): AxisRenderer<AxisPosition.Vertical.Start> {
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
        valueFormatter = { y, _, _ -> y.toInt().toString() },

        )

    return startAxis
}

@Composable
fun customBottomAxis(
    module: ModuleData,
    isDetailedView: Boolean
): AxisRenderer<AxisPosition.Horizontal.Bottom> {
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
    when (module.mName) {
        // STEPS - Total Count
        "Steps" -> {
            CartesianChartHost(
                autoScaleUp = AutoScaleUp.Full,
                chartScrollSpec = rememberChartScrollSpec(false),
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
                    startAxis = if (isDetailedView) customStartAxis(module) else null,
                    bottomAxis = customBottomAxis(module, isDetailedView),

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
            var marker = rememberMarker()


            CartesianChartHost(
                marker = rememberMarker(),
                autoScaleUp = AutoScaleUp.Full,
                chartScrollSpec = rememberChartScrollSpec(false),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 10.dp),

                chart = rememberCartesianChart(
                    layers = arrayOf(
                        rememberColumnCartesianLayer(
                            spacing = 4.dp,
                            columns = getColumns(module),
                            mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
//                                axisValueOverrider = AxisValueOverrider.fixed(maxY=10f),

                            axisValueOverrider = AxisValueOverrider.fixed(
                                maxY = if (ceil(module.stats!!.value.max) % 2 == 1f) {
                                    ceil(module.stats!!.value.max) + 1
                                } else ceil(module.stats!!.value.max)
                            )
                        ),

                        rememberLineCartesianLayer(
                            spacing = 4.dp,
                            lines = getLines(module, "Points")
                        )
                    ),
                    startAxis = if (isDetailedView) customStartAxis(module) else null,
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


/** Chart section in detailed views
 * @param module: module from DataProvider data class
 */
@Composable
fun compChart_Detailed(module: ModuleData) {
    when (module.mName) {

    // GLUCOSE and HR - Min, Max, Avg
        "Glucose", "Heart Rate" -> {
            var marker = rememberMarker()
            CartesianChartHost(
                marker = marker,
                autoScaleUp = AutoScaleUp.None,
                chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = true, initialScroll= InitialScroll.End),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 30.dp, start = 0.dp, end = 15.dp, bottom = 0.dp),

                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        spacing = 4.dp,
                        lines = getLines(module, "Line"),

                    ),
//                    persistentMarkers = mapOf(2f to marker, 3f to marker),
                    persistentMarkers = remember(marker) { mapOf(module.series_all.s_all.x.last() to marker) },

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
                        itemPlacer = remember { AxisItemPlacer.Horizontal.default(spacing = 20) },
                        valueFormatter = { x, _, _ ->
                            SimpleDateFormat("hh:mm").format(x) },

                        ),
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
