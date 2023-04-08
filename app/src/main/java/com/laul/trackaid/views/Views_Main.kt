package com.laul.trackaid


import android.app.Activity
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.laul.trackaid.connection.BloodGlucoseUpdate
import com.laul.trackaid.connection.BloodGlucoseUpdate.Companion.connectXDrip
import com.laul.trackaid.connection.GFitConnectManager
import com.laul.trackaid.data.DataGeneral
import com.laul.trackaid.data.DataGeneral.Companion.getDate
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.theme.*
import com.laul.trackaid.views.BottomNavigationBar

//import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.NavRoutes
import java.time.LocalDateTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable
@Preview
fun Header() {
    var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(1)
    var ctx = LocalContext.current

    TopAppBar(
        modifier = Modifier
            .height(175.dp)
            .background(color_surface_background),
        title = {

        }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 175.dp)
            .padding(top = 10.dp)
            .background(color_surface_background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            modifier = Modifier
                .height(height = 85.dp),
            text = getDate(Time_Now, "dd"),
            color = color_general_primary,
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .width(width = 173.dp)
                .height(height = 65.dp)
        ) {
            Text(
                modifier = Modifier
                    .height(height = 30.dp),
                text = getDate(Time_Now, "EEEE"),
                color = color_general_primary,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                modifier = Modifier
                    .height(height = 35.dp),
                text = getDate(Time_Now, "MMMM"),
                color = color_general_primary,
                style = MaterialTheme.typography.headlineLarge
            )
        }

        // Icon is used to show if all data are OK + push gluco from XDrip to Google Fit.
        Icon(
            painterResource(id = R.drawable.ic_status),
            tint = color_text_primary,
            contentDescription = "icon",
            modifier = Modifier
                .size(90.dp)
                .clickable(
                    onClick = {
                        connectXDrip(ctx)
                    }
                ),
            )

    }

}


@Composable
fun compCommon(gFitConnectManager: GFitConnectManager) {

    //    Header()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
    ) {
        composable(NavRoutes.Home.route) {
            compMainModule(gFitConnectManager, navController = navController)
        }

        composable(NavRoutes.Detailed.route + "/{moduleID}") { backStackEntry ->
            val moduleID = backStackEntry.arguments?.getString("moduleID")
            compDetailedModule(
                navController = navController,
                moduleID = moduleID
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compMainModule(gFitConnectManager: GFitConnectManager, navController: NavController) {

    Scaffold(
        topBar = { Header() },
        content = { innerPadding -> compModules(gFitConnectManager, navController, innerPadding) },
        bottomBar = { BottomNavigationBar(navController) }

    )

}

@Composable
private fun compModules(
    gFitConnectManager: GFitConnectManager,
    navController: NavController,
    innerPadding: PaddingValues
) {
    val moduleList = DataProvider.moduleList.values.toList().drop(1)

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier.background(color_surface_background),
    ) {
        items(
            items = moduleList,
            itemContent = {

                val lastDPoint = remember { it.lastDPoint }
                compModule(
                    module = it, gFitConnectManager, navController, lastDPoint!!, it.duration
                )
            })
    }
}


@Composable
private fun compModule(
    module: ModuleData,
    gFitConnectManager: GFitConnectManager,
    navController: NavController,
    lastDPoint: MutableState<LDataPoint>?,
    duration: Int
) {
    // Variables to be used to get data from GFit
    var ctx = LocalContext.current
    var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(duration)

    // Observer to trigger recomposition
    if (module.dPoints.size == 0) {
        module.getGFitData(
            permission = gFitConnectManager.permission,
            context = ctx,
            time_start = Time_Start,
            time_end = Time_End
        )
        module.getGFitData(
            permission = gFitConnectManager.permission,
            context = ctx,
            time_start = Time_End,
            time_end = Time_Now
        )
    }

    var moduleID by remember { mutableStateOf(module.mId.toString()) }


    // Card as button so that we can click on it to launch it as dedicated module
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = BorderStroke(1.dp, md_theme_light_outlineVariant),
        colors = CardDefaults.outlinedCardColors(
            containerColor = color_general_white,
            contentColor = Color(module.mColor_Primary!!)
        ),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier

            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_large),
                vertical = dimensionResource(id = R.dimen.padding_mid)
            )
            .height(height = 120.dp)
            .fillMaxWidth()
            .clickable(
                onClick = {
                    navController.navigate(NavRoutes.Detailed.route + "/$moduleID")
                },

                )
    ) {

        // Top of the card: Title + Last update timestamp
        Row(

            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_mid)
                )

        ) {
            Icon(
                painterResource(id = module.mIcon),
                tint = color_general_primary,
                contentDescription = "icon",
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_mid)
                    )
                    .size(dimensionResource(R.dimen.icon_size_large)),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = module.mName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(.7f)
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_mid)
                    )
            )


            Text(
                modifier = Modifier
                    .padding(start = dimensionResource(R.dimen.padding_mid))
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_mid)
                    )
                    .weight(.25f),
                text = getDate(lastDPoint!!.value.dateMillis_bucket, "EEE, MMM d\nh:mm a"),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall,
                color = color_text_secondary

            )

        }

        // Bottom of the card: last value + graph
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_mid)
                )
//                .border(BorderStroke(1.dp, color_general_primary))

        ) {
            // Add spacer to align the value and unit to the card title (i.e. sum of icon + spacer widths of the top row)
            Spacer(modifier = Modifier.width(42.dp))

            Column(
                modifier = Modifier.width(100.dp)
            ) {

                Text(
                    text =

                    if (module.mName == "Glucose") {
                        "%.2f".format(lastDPoint!!.value.value[0])
                    } else if (module.mName == "Pressure") {
                        "%.0f-%.0f".format(lastDPoint!!.value.value[1], lastDPoint!!.value.value[0])
                    } else {
                        "%.0f".format(lastDPoint!!.value.value[0])
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = color_general_primary,
                    modifier = Modifier
                        .padding(top = 10.dp)

                        .height(38.dp)

                )

                Text(
                    text = module.mUnit!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = color_text_secondary,

                    )


            }
            Spacer(modifier = Modifier.width(30.dp))

            compChart(module = module)
            Spacer(modifier = Modifier.width(10.dp))

        }


    }
}


@Composable
fun compLabel(module: ModuleData) {
    Card(
//        elevation = CardElevation(0.dp),
        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_large))
    ) {
        Text(
            modifier = Modifier.padding(
                vertical = 2.dp,
                horizontal = dimensionResource(R.dimen.padding_mid)
            ),
            textAlign = TextAlign.Center,
            text = "Normal",
            style = MaterialTheme.typography.bodyMedium.copy()

        )
    }
}















