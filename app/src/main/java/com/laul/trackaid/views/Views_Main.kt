package com.laul.trackaid


import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.csadev.kellocharts.view.LineChartView
import com.laul.trackaid.connection.GFitConnectManager
import com.laul.trackaid.data.DataGeneral
import com.laul.trackaid.data.DataGeneral.Companion.getDate
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.BottomNavigationBar

//import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.NavRoutes


@Composable
fun compCommon(gFitConnectManager: GFitConnectManager) {

    val navController = rememberNavController()



    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
    ) {
        composable(NavRoutes.Home.route) {
            compMainModule(gFitConnectManager, navController = navController)
        }

        composable(NavRoutes.Detailed.route) {
            compDetailedModule(navController = navController)
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compMainModule(gFitConnectManager: GFitConnectManager, navController : NavHostController) {

    Scaffold(
        content = { innerPadding -> compModules(gFitConnectManager, navController, innerPadding)   },
        bottomBar = {BottomNavigationBar(navController)}

    )
}

@Composable
private fun compModules(gFitConnectManager: GFitConnectManager, navController: NavHostController, innerPadding: PaddingValues) {

    LazyColumn(
        contentPadding = innerPadding

    ) {
        items(
            items = DataProvider.moduleList,

            itemContent = {
                compModule(module = it, gFitConnectManager, navController )
            })
    }
}

@Composable
private fun compModule(module: ModuleData, gFitConnectManager: GFitConnectManager, navController: NavHostController) {

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
            navController.navigate(NavRoutes.Detailed.route)

            module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_Start,time_end = Time_End        )
            module.getGFitData(permission = gFitConnectManager.permission,context = context,lastCall = lastCall,time_start = Time_End,time_end = Time_Now        )
        },
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_mid)),
//        elevation = elevation(defaultElevation = 0.dp, pressedElevation = -2.dp)

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
                    )
                    Text(
                        text = module.mName,
//                        style = MaterialTheme.typography.titleLarge.copy(),
                        modifier = Modifier
                            .padding(start = dimensionResource(R.dimen.padding_mid))
                            .align(Alignment.Bottom),

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
                    style = MaterialTheme.typography.titleLarge.copy(),

                )
            }
            else{
                Text(
                    text = "%.2f".format(lastDPoint.value[0]),
                    style = MaterialTheme.typography.titleLarge.copy(),

                )
            }
        }
        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_mid)),
            text = module.mUnit,
            style = MaterialTheme.typography.titleMedium.copy(),

        )
    }

    // Display Date of last value(s)
    Text(
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