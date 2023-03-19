package com.laul.trackaid

import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.csadev.kellocharts.model.copy
import co.csadev.kellocharts.view.LineChartView
import com.laul.trackaid.data.ModuleData


@Composable
fun compChart(context: Context, module: ModuleData){
    module.formatAsColumn()
    module.kChart_Data.axisXBottom=null
    module.kChart_Data.axisYRight=null


    AndroidView(
//        modifier = Modifier
//            .width(100.dp)
//            .height(90.dp),
        factory = { ctx: Context ->
            //  Initialize a View or View hierarchy here
            LineChartView(ctx).apply {
                lineChartData = module.kChart_Data
            }
        },
        update = {
            it.lineChartData = module.kChart_Data
            val tempViewport = it.maximumViewport.copy()
            tempViewport.inset(-tempViewport.width() * 0.05f, -tempViewport.height() * 0.05f)
            it.maximumViewport = tempViewport
            it.currentViewport = tempViewport
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
        update = {
            it.lineChartData = module.kChart_Data
        }

    )
}