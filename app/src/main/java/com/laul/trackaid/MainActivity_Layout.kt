package com.laul.trackaid


import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import co.csadev.kellocharts.model.Axis
import co.csadev.kellocharts.model.Line
import co.csadev.kellocharts.model.LineChartData
import co.csadev.kellocharts.model.PointValue
import co.csadev.kellocharts.view.LineChartView
import com.laul.trackaid.DataGeneral.Companion.getDate

import com.laul.trackaid.ui.theme.backgroundColor


@Composable
fun compMainModule(gFitConnectManager: GFitConnectManager) {
    Scaffold(
        backgroundColor = backgroundColor,
        content = {
            compModules(gFitConnectManager)
        }
    )
}

@Composable
private fun compModules(gFitConnectManager: GFitConnectManager) {
    LazyColumn(
        modifier = Modifier.padding(
            vertical = 10.dp,
            horizontal = 5.dp
        )
    ) {
        items(
            items = DataProvider.moduleList,

            itemContent = {
                compModule(module = it, gFitConnectManager)
            })
    }
}

@Composable
private fun compModule(module: ModuleData, gFitConnectManager: GFitConnectManager) {

    // Variables to be used to get data from GFit
    var context = LocalContext.current
    var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(6)

    // Observer to trigger recomposition
    val lastCall = remember { mutableStateOf(0f.toLong() )    }

    if (module.dPoints.size == 0) {
        module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_Start,time_end = Time_End        )
        module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_End,time_end = Time_Now        )
    }

    // Card as button so that we can click on it to launch it as dedicated module
    OutlinedButton(
        onClick = {
            module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_Start,time_end = Time_End        )
            module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_End,time_end = Time_Now        )
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(module.mColor_Secondary).copy(
                alpha = 0.1f
            )
        ),
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_mid)),
        border = BorderStroke(
            1.dp,
            colorResource(id = module.mColor_Primary)
        ),
        // elevation = elevation(defaultElevation = 1.dp, pressedElevation = 0.dp)

    ) {
        // Structure for module box
        Row(
            modifier = Modifier.padding(
                vertical = dimensionResource(id = R.dimen.padding_mid),
                horizontal = dimensionResource(id = R.dimen.padding_small)
            )
        ) {

            // Left side - Contains title, latest value(s) and label
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 5.dp)
            ) {
                // Title and icon with primary color/tint
                Row(
                ) {
                    Icon(
                        painterResource(id = module.mIcon),
                        contentDescription = "Favorite",
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size)),
                        tint = colorResource(module.mColor_Primary)
                    )
                    Text(
                        text = module.mName,
                        style = MaterialTheme.typography.h1.copy(),
                        modifier = Modifier
                            .padding(start = dimensionResource(R.dimen.padding_mid))
                            .align(Alignment.Bottom),
                        color = colorResource(id = module.mColor_Primary)

                    )
                }

                // Latest value + unit + associated date
                compLastData(module, lastCall)

                // Label for warning / normal info based on last value
                compLabel(module)
            }

            // Right side - Contains chart
            // module.formatAsColumn()

            Column(){
                //compChart(context, module)
                var ctx = LocalContext.current

                Text(text = lastCall.value.toString())
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
//                        module.kYAxis = Axis(hasLines = true, maxLabels = 4)
//                        module.kYAxis.name = lastCall.value.toString()
                    }

                )





            }
        }
    }
}


@Composable
fun compLastData(module: ModuleData, lastCall: MutableState<Long>) {

    var lastDPoint = module.getLastData()


    Row(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_large))) {
        if (lastCall.value != 0L) {
            // Display last value(s)
            if (module.mName == "Blood Pressure"){
                Text(
                    text = "%.0f-%.0f".format(lastDPoint.value[1], lastDPoint.value[0]),
                    style = MaterialTheme.typography.h1.copy(),
                    color = colorResource(id = module.mColor_Primary)

                )
            }
            else{
                Text(
                    text = "%.2f".format(lastDPoint.value[0]),
                    style = MaterialTheme.typography.h1.copy(),
                    color = colorResource(id = module.mColor_Primary)

                )
            }
        }
        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_mid)),
            text = module.mUnit,
            style = MaterialTheme.typography.h2.copy(),
            color = colorResource(id = module.mColor_Primary)

        )
    }
    // Display Date of last value(s)
    Text(
        text = getDate(lastDPoint.dateMillis_bucket, "EEE, MMM d - h:mm a "),
        style = MaterialTheme.typography.h3.copy(),
        color = colorResource(id = module.mColor_Primary)
    )
}

@Composable
fun compLabel(module: ModuleData) {
    Card(
        border = BorderStroke(
            1.dp,
            colorResource(id = module.mColor_Primary)
        ),
        backgroundColor = (colorResource(id = R.color.state_normal)),
        elevation = 0.dp,
        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_large))
    ) {
        Text(
            color = colorResource(id = R.color.grey_secondary),
            modifier = Modifier.padding(
                vertical = 2.dp,
                horizontal = dimensionResource(R.dimen.padding_mid)
            ),
            textAlign = TextAlign.Center,
            text = "Normal",
            style = MaterialTheme.typography.body1.copy()

        )
    }
}