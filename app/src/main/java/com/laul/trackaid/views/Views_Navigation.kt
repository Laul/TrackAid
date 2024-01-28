package com.laul.trackaid.views

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.laul.trackaid.R
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.NavRoutes
import com.laul.trackaid.theme.*
import kotlinx.coroutines.launch

@Composable
fun getHealthConnectPermissions(context : Context) : Pair<HealthConnectClient, Boolean> {
    // Create client
    val client = HealthConnectClient.getOrCreate(context = context )

    // Create list of permissions to request
    val permissionsSet = mutableSetOf<String>()

    DataProvider.moduleList.values.toList().drop(1).forEach{
        permissionsSet.add(HealthPermission.getReadPermission(it.recordType!!))
    }

    // Create the permissions launcher.
    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()
    var permissionGranted by remember {
        mutableStateOf(false)
    }
    // Create a CoroutineScope bound to the request for HealthConnect
    val coroutineScope = rememberCoroutineScope()

    val requestPermissions = rememberLauncherForActivityResult(
        requestPermissionActivityContract
    ) {
            granted -> permissionGranted =  granted.containsAll(permissionsSet)
    }

    // Permissions management for healthconnect
    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) {
            coroutineScope.launch {
                val granted = client.permissionController
                    .getGrantedPermissions()
                if (granted.containsAll(permissionsSet)) {
                    permissionGranted = true

                } else {
                    // Permissions not granted, request permissions.
                    requestPermissions.launch(permissionsSet)
                }
            }
        }
    }

    return Pair(client, permissionGranted)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(navController: NavController, moduleID: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 75.dp)
            .padding(top = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            colors = IconButtonColors(
                contentColor = color_general_primary,
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = color_text_secondary
            ),
            onClick = { navController.navigateUp() }) {
            Icon(Icons.Filled.ArrowBack, null)
        }
        Text(
            text = DataProvider.moduleList[NavRoutes.Detailed.route + "/$moduleID"]!!.mName,
            color = color_text_primary,
            maxLines = 1,
            textAlign = TextAlign.Center

        )
    }

}



/** Bottom Navigation bar used across the entire application
 * @param navController: Manager for bottom navigation bar
 */
@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar(

        containerColor = color_surface_background,
        modifier = Modifier
            .background(color_surface_background)
            .height(65.dp)
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val detailedID = navBackStackEntry?.arguments?.getString("moduleID")

        DataProvider.moduleList.forEach { item ->
            var selected = false
            if (currentRoute != null) {
                if (currentRoute.startsWith("Detailed") && item.key.startsWith("Detailed")) {
                    if (detailedID != null) {
                        selected = item.key.endsWith(detailedID)
                    }
                } else {
                    selected =
                        currentRoute == item.key // if current route equal to screen route it return true
                }
            }

            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = color_surface_background
                ),
                alwaysShowLabel = true,
                selected = selected,
                label = {
                    Text(
                        text = item.value.mName,
                        fontSize = 12.sp,
                    )
                },
                icon = {
                    Icon(
                        ImageVector.vectorResource(
                            id = if (selected) item.value.mIcon else item.value.mIcon_outlined
                        ),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(0.dp)
                            .size(dimensionResource(id = R.dimen.icon_size_large))
                    )
                },


                onClick = {
                    navController.navigate(item.key) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = false
                            }
                        }
                        launchSingleTop = true
                        restoreState = false
                    }

                })

        }
    }
}

