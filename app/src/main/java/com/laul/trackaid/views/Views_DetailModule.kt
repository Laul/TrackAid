package com.laul.trackaid

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.theme.color_surface_background
import com.laul.trackaid.theme.color_text_primary
import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.NavRoutes
import com.laul.trackaid.views.TopNavigationBar
import com.laul.trackaid.views.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.vertical.createVerticalAxis
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.marker.Marker


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compDetailedModule(navController: NavHostController, moduleID : String?){
    Scaffold(
        containerColor = color_surface_background,
        topBar = { TopNavigationBar(navController, moduleID) },
        content = {innerPadding -> compDetailed(navController, innerPadding, moduleID) },
        bottomBar = {BottomNavigationBar(navController)}
    )
}

@Composable
fun compDetailed(navController: NavHostController, innerPaddingValues: PaddingValues, moduleID : String?){
    Row(
        modifier = Modifier
            .padding(innerPaddingValues)
            .background(color_surface_background)
    ) {
        compChart(
            module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!! ,
            isBottomAxis = true,
            backgroundColor = color_surface_background
            )

    }


}
