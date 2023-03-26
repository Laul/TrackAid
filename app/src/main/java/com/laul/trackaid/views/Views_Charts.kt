package com.laul.trackaid

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.csadev.kellocharts.model.Line
import co.csadev.kellocharts.model.LineChartData
import co.csadev.kellocharts.model.copy
import co.csadev.kellocharts.view.LineChartView
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.theme.color_general_primary


@Composable
fun compChart(context: Context, module: ModuleData){
    module.formatAsColumn()
    module.kChart_Data.axisXBottom=null
    module.kChart_Data.axisYRight=null


    AndroidView(

        modifier = Modifier
            .width(200.dp)
            .height(75.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            ,

        factory = { ctx: Context ->
            //  Initialize a View or View hierarchy here
            LineChartView(ctx).apply {
                lineChartData = module.kChart_Data
            }

        },
        update = { chart ->
            chart.lineChartData = module.kChart_Data
            val tempViewport = chart.maximumViewport.copy()

            val sumVal  = module.kCol.sumOf{ it.values[1].y.toInt() }
            if( sumVal == 0|| module.kCol.size == 0) {
                tempViewport.bottom = -15f
                tempViewport.top = 100f
                tempViewport.inset(-tempViewport.width() * 0.05f, 0f)


                chart?.maximumViewport = tempViewport
                chart?.currentViewport = tempViewport
            }

            else {
                tempViewport.inset(-tempViewport.width() * 0.05f, -tempViewport.height() * 0.09f)
                chart.maximumViewport = tempViewport
                chart.currentViewport = tempViewport
            }
            chart.lineChartData.lines.forEach {
                it.color = module.mColor_Primary!!

            }

        }

    )
}

@Composable
fun compChart_Detailed(context: Context, module: ModuleData){
    AndroidView(


        modifier = Modifier.height(150.dp),
        factory = { ctx: Context ->


            //  Initialize a View or View hierarchy here
            LineChartView(ctx).apply {
                lineChartData = module.kChart_Data
            }

        },
        update = { chart ->
            chart.lineChartData = module.kChart_Data
            val tempViewport = chart.maximumViewport.copy()

            val sumVal = module.kCol.sumOf { it.values[1].y.toInt() }
            if (sumVal == 0 || module.kCol.size == 0) {
                tempViewport.bottom = -10f
                tempViewport.top = 100f
                tempViewport.inset(-tempViewport.width() * 0.05f, 0f)


                chart?.maximumViewport = tempViewport
                chart?.currentViewport = tempViewport
            } else {
                tempViewport.inset(-tempViewport.width() * 0.05f, -tempViewport.height() * 0.07f)
                chart.maximumViewport = tempViewport
                chart.currentViewport = tempViewport
            }
        }
    )
}