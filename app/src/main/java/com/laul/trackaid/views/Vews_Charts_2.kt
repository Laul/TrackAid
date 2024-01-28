package com.laul.trackaid.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.laul.trackaid.data.ModuleData
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel



    @Composable
    public fun ColumnChart1(modifier: Modifier = Modifier, module: ModuleData) {
        val model =
            CartesianChartModel(
                ColumnCartesianLayerModel.build { module.series_all.s_sumD  },
                // LineCartesianLayerModel.build { series(4, 1, 8, 12, 5) },
            )
        CartesianChartHost(
            chart =
            rememberCartesianChart(
                getColumnLayer(),
                bottomAxis = rememberBottomAxis(),
            ),
            model = model,
            modifier = modifier,
        )
    }

    @Composable
    private fun getColumnLayer(verticalAxisPosition: AxisPosition.Vertical? = null) =
        rememberColumnCartesianLayer(
            columns =
            listOf(
                rememberLineComponent(
                    color = Color.Black,
                    thickness = 8.dp,
                    shape = Shapes.pillShape,
                ),
            ),
            verticalAxisPosition = verticalAxisPosition,
        )


