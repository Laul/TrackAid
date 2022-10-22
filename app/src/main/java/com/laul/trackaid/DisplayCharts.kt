package com.laul.trackaid

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.csadev.kellocharts.model.Axis
import co.csadev.kellocharts.model.Line
import co.csadev.kellocharts.model.LineChartData
import co.csadev.kellocharts.model.PointValue
import co.csadev.kellocharts.view.LineChartView



@Composable
fun compChart(context: Context, module: ModuleData){
    AndroidView(

        modifier = Modifier.size(200.dp),
        factory = { ctx: Context ->


            //  Initialize a View or View hierarchy here
            LineChartView(ctx).apply {
                lineChartData = module.kChart_Data
            }

        },
        update = {
            it.lineChartData = module.kChart_Data
        }

    )
}