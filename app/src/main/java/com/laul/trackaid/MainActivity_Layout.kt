package com.laul.trackaid


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laul.trackaid.ui.theme.TrackAidTheme
import androidx.compose.material.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.laul.trackaid.ui.theme.backgroundColor


@Composable
fun MainModule(name: String) {
    var moduleTitles = arrayListOf<String>("Blood Glucose", "Steps", "Heart Rate", "Blood Pressure")
    Scaffold(
        backgroundColor = backgroundColor,
        content = {
            Modules()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun Main_ModulePreview() {

    TrackAidTheme {
        MainModule("Android")
    }
}


@Composable
private fun Modules() {
    val modules = remember { DataProvider.moduleList }

    LazyColumn(
        modifier = Modifier.padding(
            vertical = 10.dp,
            horizontal = 5.dp
        )
    ) {
        items(
            items = modules,

            itemContent = {
               Module(module = it)
            })
    }
}


@Composable
private fun Module(module: ModuleData) {
//
//    Surface(
//        modifier = Modifier
//            .padding(
//                vertical = dimensionResource(R.dimen.padding_mid),
//                horizontal = dimensionResource(R.dimen.padding_mid)
//            )
//            .border(
//                BorderStroke(
//                    1.dp,
//                    colorResource(id = module.mColor_Primary)
//                ),
//
//                ),
//        color = colorResource(id = module.mColor_Secondary),
//        contentColor = colorResource(module.mColor_Primary)
//
//    ){


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
                vertical =dimensionResource(id = R.dimen.padding_mid),
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
                LatestValue(module)

                // Label for warning / normal info based on last value
                Label(module)
            }


        }
    }
}


@Composable
private fun LatestValue(module: ModuleData) {
    Row(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_large))) {
        Text(
            text = "Value",
            style = MaterialTheme.typography.h2.copy()
        )
        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_mid)),
            text = module.mUnit,
            style = MaterialTheme.typography.h2.copy()
        )
    }
    Text(
        text = "Date",
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