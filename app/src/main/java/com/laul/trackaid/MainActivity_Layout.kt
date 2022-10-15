package com.laul.trackaid


import android.util.Log
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

import com.laul.trackaid.ui.theme.backgroundColor


@Composable
fun MainModule(gFitConnectManager: GFitConnectManager) {


    Scaffold(
        backgroundColor = backgroundColor,
        content = {
            Modules(gFitConnectManager)
        }
    )
}

@Composable
fun TextDebug(result: Float) {

    Text(
        text = result.toString(),
        style = MaterialTheme.typography.h1.copy()
    )
}

@Composable
private fun Modules(gFitConnectManager: GFitConnectManager) {
    val modules = remember { mutableStateOf( DataProvider.moduleList)}

    LazyColumn(
        modifier = Modifier.padding(
            vertical = 10.dp,
            horizontal = 5.dp
        )
    ) {
        items(
            items = DataProvider.moduleList,

            itemContent = {
                Module(module = it, gFitConnectManager)
            })
    }
}


@Composable
private fun Module(module: ModuleData, gFitConnectManager: GFitConnectManager) {
    var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(5)
    val lastCall = remember { mutableStateOf(0f.toLong() )    }
    Log.i("lastCall", lastCall.toString())


    if (module.dPoints.size == 0) {
        module.getGFitData(
            gFitConnectManager.permission,
            LocalContext.current,
            lastCall,
            Time_Start,
            Time_End
        )
    }


    OutlinedButton(
        onClick = { /* TODO to handle */ },
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
        Row(
            modifier = Modifier.padding(
                vertical = dimensionResource(id = R.dimen.padding_mid),
                horizontal = dimensionResource(id = R.dimen.padding_small)
            )
        ) {

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
                            .align(Alignment.Bottom)
                    )
                }

                // Latest value + unit + associated date
                LatestValue(module, lastCall)

                // Label for warning / normal info based on last value
                Label(module)
            }


        }
    }
}


@Composable
        fun LatestValue(module: ModuleData, lastCall: MutableState<Long>) {
            Row(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_large))) {
        if (lastCall.value !=0L){
            Text(
                text = module.dPoints[0].value.toString(), //module.dPoint[0].value[0].toString(),
                style = MaterialTheme.typography.h2.copy()
            )
        }
        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_mid)),
            text = module.mUnit,
            style = MaterialTheme.typography.h2.copy()
        )
    }
    Text(
        text = module.mName,
        style = MaterialTheme.typography.h3.copy()
    )
}

@Composable
fun Label(module: ModuleData) {
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