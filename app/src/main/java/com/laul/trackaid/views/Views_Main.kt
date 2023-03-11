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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
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

        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_mid)),

    ) {
        // Structure for module box
        Row(
            modifier = Modifier
                .clickable(
                    onClick = {
                        navController.navigate(NavRoutes.Detailed.route + "/$moduleID")
                    },
                )
                .background(Color(0xFFF3F3F3))
                .padding(
                    vertical = dimensionResource(id = R.dimen.padding_large),
                    horizontal = dimensionResource(id = R.dimen.padding_large)
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
                    modifier = Modifier.background(Color(0xFFF3F3F3))
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

                compChart(context = ctx, module = module )
//                AndroidView(
//
//                    modifier = Modifier.size(200.dp),
//                    factory = { ctx: Context ->
//
//
//                        //  Initialize a View or View hierarchy here
//                        LineChartView(ctx).apply {
//                            lineChartData = module.kChart_Data
//                        }
//
//                    },
//                    update = {
//                        it.lineChartData = module.kChart_Data
////                        module.kYAxis = Axis(hasLines = true, maxLabels = 4)
////                        module.kYAxis.name = lastCall.value.toString()
//                    }
//
//                )





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
            text = module.mUnit!!,
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
















//
//@Composable
//fun NavigationBar() {
//    NavigationBar(
//        containerColor = Color(0xfffffbfe),
//        contentColor = Color(0xff1c1b1f)
//    ) {
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.home),
//                    contentDescription = "Icon",
//                    tint = Color(0xff1c1b1f))
//            },
//            label = { Text(text = "Home") },
//            colors = NavigationBarItemDefaults.colors(
//                selectedIconColor = Color(0xff1c1b1f),
//                selectedTextColor = Color(0xff1c1b1f)
//            ),
//            alwaysShowLabel = true,
//            selected = true,
//            onClick = { })
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.bloodglucose_outline),
//                    contentDescription = "Icon",
//                    tint = Color(0xff1c1b1f))
//            },
//            label = { Text(text = "Glucose") },
//            alwaysShowLabel = true,
//            selected = false,
//            onClick = { })
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.steps_outlined),
//                    contentDescription = "Icon",
//                    tint = Color(0xff1c1b1f))
//            },
//            label = { Text(text = "Steps") },
//            alwaysShowLabel = true,
//            selected = false,
//            onClick = { })
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.heartrate_outlined),
//                    contentDescription = "Icon",
//                    tint = Color(0xff1c1b1f))
//            },
//            label = { Text(text = "Heart") },
//            alwaysShowLabel = true,
//            selected = false,
//            onClick = { })
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.bloodpressure_outlined),
//                    contentDescription = "Icon",
//                    tint = Color(0xff1c1b1f))
//            },
//            label = { Text(text = "Pressure") },
//            alwaysShowLabel = true,
//            selected = false,
//            onClick = { })
//    }
//}