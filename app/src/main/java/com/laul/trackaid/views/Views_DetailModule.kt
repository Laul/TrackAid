package com.laul.trackaid

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.NavRoutes
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.vertical.createVerticalAxis
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf
private val chartModifier = Modifier.height(400.dp)

private val model = entryModelOf(
    entriesOf(0 to 1f, 1 to -2f),
    entriesOf(0 to 2f, 1 to 3f),
)

private val columns: List<LineComponent>
    @Composable
    get() = listOf(
        lineComponent(
            color = Color.Transparent,
            thickness = 8.dp,
            shape = RoundedCornerShape(4.dp),
            ),
        lineComponent(
            thickness = 8.dp,
            shape = RoundedCornerShape(4.dp),
            dynamicShader = verticalGradient(arrayOf(colors.primary, colors.secondary)),),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compDetailedModule(navController: NavHostController, moduleID : String?){

    Scaffold(
        content = {innerPadding -> compDetailed(navController, innerPadding, moduleID) },
        bottomBar = {BottomNavigationBar(navController)}
    )
}

@Composable
fun compDetailed(navController: NavHostController, innerPaddingValues: PaddingValues, moduleID : String?){
    var ctx = LocalContext.current
    StackedColumnChartWithNegativeValuesAndDataLabels(
        module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!!
    )
}


//@Preview
@Composable
public fun StackedColumnChartWithNegativeValuesAndDataLabels(module: ModuleData) {
    androidx.compose.material.Surface {
        Chart(
            modifier = chartModifier,
            autoScaleUp = AutoScaleUp.None ,
            chart = columnChart(
                columns = columns,
/*
                dataLabel = com.patrykandpatrick.vico.core.component.text.textComponent(),
*/
                mergeMode = ColumnChart.MergeMode.Stack,
            ),
            model = module.cChartModel,
            startAxis = createVerticalAxis {
                label = textComponent(
                    color = colors.primary,
                    textSize = 10.sp,
                    background = shapeComponent(
                        shape = CutCornerShape(
                            CornerSize(percent = 25),
                            CornerSize(percent = 50),
                            CornerSize(percent = 50),
                            CornerSize(percent = 25),
                        ),
                        color = colors.primary.copy(alpha = 0.1f),
                    ),
                    padding = dimensionsOf(end = 8.dp, start = 4.dp),
                )
                axis = null
                tick = null
                guideline = LineComponent(
                    colors.primary.copy(alpha = 0.1f).toArgb(),
                    1.dp.value,
                )
            },
            bottomAxis = bottomAxis(),
        )
    }
}

@Composable
private fun SampleCard(
    chart: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.material.Card(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            chart()
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material.Text(
                text = "Title",
                style = MaterialTheme.typography.h6,
            )
            androidx.compose.material.Text(
                text = "This is a subtitle. It may be long.",
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}

