package com.laul.trackaid


import android.content.Context
import android.media.Image
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
import androidx.compose.ui.draw.clip
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
import com.laul.trackaid.connection.GFitConnectManager
import com.laul.trackaid.data.DataGeneral
import com.laul.trackaid.data.DataGeneral.Companion.getDate
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.theme.*
import com.laul.trackaid.views.BottomNavigationBar

//import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.NavRoutes
@OptIn(ExperimentalMaterial3Api::class)
@Composable @Preview
fun Header() {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(150.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp)
            ) {
                val badgeNumber = "A"
                Badge(
                    contentColor = Color(0xfffffbff)
                ) {
                    Text(
                        text = badgeNumber)
                }
                Spacer(
                    modifier = Modifier
                        .width(width = 16.dp))
                Column(
                    modifier = Modifier
                        .width(width = 192.dp)
                ) {
                    Text(
                        text = "Welcome Lauranne",
                        color = Color(0xff201a1b),
                        lineHeight = 24.sp,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.15.sp),
                        modifier = Modifier
                            .width(width = 192.dp))
                    Spacer(
                        modifier = Modifier
                            .height(height = 4.dp))
//                    Text(
//                        text = "Subhead",
//                        color = Color(0xff201a1b),
//                        textAlign = TextAlign.Center,
//                        lineHeight = 20.sp,
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            letterSpacing = 0.25.sp),
//                        modifier = Modifier
//                            .width(width = 57.dp))
                }
            }

    }

//    TopAppBar(
//        modifier = Modifier.height(200.dp),
//        title = {
//            Column(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.End,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(height = 88.dp)
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .width(width = 346.dp)
//                        .height(height = 67.dp)
//                        .padding(horizontal = 20.dp,
//                            vertical = 15.dp)
//                ) {
//                    Text(
//                        text = "LaulAid",
//                        color = Color(0xff673c4f),
//                        style = TextStyle(
//                            fontSize = 35.sp,
//                            fontWeight = FontWeight.Bold),
//                        modifier = Modifier
//                            .height(height = 59.dp))
//                }
//                Spacer(
//                    modifier = Modifier
//                        .height(height = -18.dp))
//                Text(
//                    text = "Welcome",
//                    color = Color(0xff673c4f),
//                    lineHeight = 36.sp,
//                    style = MaterialTheme.typography.headlineSmall,
//                    modifier = Modifier
//                        .height(height = 59.dp))
//            }
//        })
}


@Composable
fun compCommon(gFitConnectManager: GFitConnectManager) {
    Header()

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
    ) {
        composable(NavRoutes.Home.route) {
            compMainModule(gFitConnectManager, navController = navController)
        }

        composable(NavRoutes.Detailed.route +  "/{moduleID}") {backStackEntry ->
            val moduleID = backStackEntry.arguments?.getString("moduleID")
            compDetailedModule(
                navController = navController ,
                moduleID = moduleID
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compMainModule(gFitConnectManager: GFitConnectManager, navController : NavController) {
        Scaffold(
            topBar = { Header()},
            content = { innerPadding -> compModules(gFitConnectManager, navController, innerPadding)   },
            bottomBar = {BottomNavigationBar(navController)}

        )

}

@Composable
private fun compModules(gFitConnectManager: GFitConnectManager, navController: NavController, innerPadding: PaddingValues) {
    val moduleList = DataProvider.moduleList.values.toList().drop(1)

    LazyColumn(
        contentPadding = innerPadding

    ) {
        items(
            items = moduleList,
            itemContent = {
                compModule(
                    module = it, gFitConnectManager, navController )
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun compModule(module: ModuleData, gFitConnectManager: GFitConnectManager, navController: NavController) {

    // Variables to be used to get data from GFit
    var context = LocalContext.current
    var (Time_Now, Time_Start, Time_End) = DataGeneral.getTimes(6)

    // Observer to trigger recomposition
    val lastCall = remember { mutableStateOf(0f.toLong() )    }
//    if (module.dPoints.size == 0) {
        module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_Start,time_end = Time_End        )
        module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_End,time_end = Time_Now        )
//    }
    var moduleID by remember { mutableStateOf(module.mId.toString()) }

    // Card as button so that we can click on it to launch it as dedicated module
    Card(
        border =BorderStroke(1.dp,md_theme_light_outlineVariant ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = md_theme_light_surfaceVariant ,

        ),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier

            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_large),
                vertical = dimensionResource(id = R.dimen.padding_mid)
            )
            .height(height = 120.dp)

//            .padding(top = 23.dp,
//                bottom = 28.dp)

    ) {
        // Structure for module box
        Row(
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(
                    vertical = dimensionResource(id = R.dimen.padding_mid),
                    horizontal = 20.dp
                )
//                    horizontal = dimensionResource(id = R.dimen.padding_large))

                .height(height = 110.dp)

                .clickable(
                    onClick = {
                        navController.navigate(NavRoutes.Detailed.route + "/$moduleID")
                    },
                )
        ) {
            // Icon
            Badge(
                modifier = Modifier

                    .align(Alignment.CenterVertically)
                    .size(size = 50.dp)
                    .clip(shape = RoundedCornerShape(percent = 50))
                    .background(color = Color.Green),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painterResource(id = module.mIcon),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.icon_size))
                        .padding(dimensionResource(id = R.dimen.padding_small))
                )

            }



            // Left side - Contains title, latest value(s) and label
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_mid))
                    .weight(.7f)
            ) {
                // Module Title
                Text(
                    text = module.mName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(start = dimensionResource(R.dimen.padding_mid))
                )

                // Latest value + unit + associated date
                compLastData(module, lastCall)

//
//                    // Label for warning / normal info based on last value
//                    compLabel(module)
//
                    }



//            // Right side - Contains chart

            Column(
                modifier=Modifier
                    .weight(.3f)
                    .height(60.dp)

        ){
                var ctx = LocalContext.current

                Text(
                    modifier = Modifier.height(0.dp),
                    text = lastCall.value.toString()
                )

                compChart(context = ctx, module = module )
            }
        }
    }
}


@Composable
fun compLastData(module: ModuleData, lastCall: MutableState<Long>) {

    var lastDPoint = module.getLastData()

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .width(width = 178.dp)
            .height(height = 40.dp)
            .padding(start = dimensionResource(R.dimen.padding_mid))
    ) {

        if (lastCall.value != 0L) {
            // Display last value(s)
            if (module.mName == "Blood Pressure") {
                Text(
                    text = "%.0f-%.0f".format(lastDPoint.value[1], lastDPoint.value[0]),
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.displaySmall,
                )
            } else {
                Text(
                    text = "%.2f".format(lastDPoint.value[0]),
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.displaySmall,
                )
            }
        }
        Spacer(
            modifier = Modifier
                .width(width = 3.dp)
        )


        Text(
            modifier = Modifier.padding(bottom = 1.7.dp),
            text = module.mUnit!!,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )

    }
    // Display Date of last value(s)
    Text(
        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_mid)),
        text = getDate(lastDPoint.dateMillis_bucket, "EEE, MMM d - h:mm a "),
        style = MaterialTheme.typography.titleSmall.copy(),
    )
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















