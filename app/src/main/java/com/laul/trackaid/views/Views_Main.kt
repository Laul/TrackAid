package com.laul.trackaid


//import com.laul.trackaid.views.BottomNavigationBar
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.DataProvider.Companion.healthConnectUpdate
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.data.NavRoutes
import com.laul.trackaid.theme.*
import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.getHealthConnectPermissions
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/** Header - Current date + button to retrieve gluco from XDrip and push it to Google Fit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 175.dp)
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            modifier = Modifier
                .height(height = 85.dp),
            text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd")),
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
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE")),
                color = color_general_primary,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                modifier = Modifier
                    .height(height = 35.dp),
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM")),
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

                    }
                ),
            )

    }

}

/** Navhost management : dispatch to proper view
 */
@Composable
fun compCommon(context: Context) {
    // Get HealthConnect Permissions Granting
    val (client, permissionGranted) = getHealthConnectPermissions(context)

    // If permissions are granted, we display the home view
    if (permissionGranted) {

        // If permissions are true, we retrieve data
        LaunchedEffect(permissionGranted) {
            DataProvider.healthConnectUpdate(client )
        }

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
        ) {
            composable(NavRoutes.Home.route) {
                compMainModule(navController = navController, client= client)
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
}

/** View structure for Main screen
 * @param navController: Manager for bottom navigation bar
 * @param client: client to healthconnect
 */
@Composable
fun compMainModule(navController: NavController,client: HealthConnectClient) {


    Scaffold(
        containerColor = color_surface_background,
        topBar = { Header() },
        content = { innerPadding -> compModules( navController, client, innerPadding) },
        bottomBar = { BottomNavigationBar(navController) }

    )

}

/** List of displayed modules in main view
 * @param navController: Manager for bottom navigation bar
 * @param client: client to healthconnect
 * @param innerPadding: Int value for lazycolumn padding
 */
@Composable
private fun compModules(
    navController: NavController,
    client: HealthConnectClient,
    innerPadding: PaddingValues
) {
    val moduleList = DataProvider.moduleList.values.toList().drop(1)

    // Pull to refresh support: we call healthconnectUpdate when pulling the lazycolumn
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(refreshing) {
        if (refreshing) {
            healthConnectUpdate(client)
            delay(50)
            refreshing = false
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = { refreshing = true },
    )
    {
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.background(color_surface_background),
        ) {
            items(
                items = moduleList,
                itemContent = {

                    // lastDPoint is used as value to compare or refreshing the UI
                    val lastDPoint = remember { it.lastDPoint }

                    compModule(
                        module = it,
                        navController = navController,
                        lastDPoint = lastDPoint!!
                    )
                }
            )
        }
    }
}

/** Content of each module in the lazycolumn
 * @param module: module from DataProvider data class
 * @param gFitConnectManager: Manager to retrieve Google data
 * @param lastDPoint: last data point of current module
 * @param duration: Int value that represents the duration to retrieve data
 */
@Composable
private fun compModule(
    module: ModuleData,
    navController: NavController,
    lastDPoint: MutableState<LDataLastPoint>?
) {
    // Variables to be used to get data from GFit
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
                text = lastDPoint!!.value.date,
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

        ) {
            // Add spacer to align the value and unit to the card title (i.e. sum of icon + spacer widths of the top row)
            Spacer(modifier = Modifier.width(42.dp))

            Column(
                modifier = Modifier.width(100.dp)
            ) {

                Text(
                    text =

                    if (module.mName == "Glucose") {
                        "%.2f".format(lastDPoint!!.value.value)
                    }
                    //else if (module.mName == "Pressure") {
//                        "%.0f-%.0f".format(lastDPoint!!.value.value[1], lastDPoint!!.value.value[0])
                //    }
            else {
                        "%.0f".format(lastDPoint!!.value.value)
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
//
            if  (module.series_all.s_avg.y.isNotEmpty() || module.series_all.s_sumD.y.isNotEmpty() ) {
                compChart(
                    module = module,
                    isDetailedView = false,
                    backgroundColor = color_general_white
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

        }


    }
}

//
///** Label to display the "status" of a given module
// * @param module: module from DataProvider data class
// */
//@Composable
//fun compLabel(module: ModuleData) {
//    Card(
//        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_large))
//    ) {
//        Text(
//            modifier = Modifier.padding(
//                vertical = 2.dp,
//                horizontal = dimensionResource(R.dimen.padding_mid)
//            ),
//            textAlign = TextAlign.Center,
//            text = "Normal",
//            style = MaterialTheme.typography.bodyMedium.copy()
//
//        )
//    }
//}
//
//
//
//











