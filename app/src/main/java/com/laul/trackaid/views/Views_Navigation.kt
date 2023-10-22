package com.laul.trackaid.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.laul.trackaid.R
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(navController: NavController, moduleID: String?) {


    TopAppBar(
        title = {

            Text(

                text = DataProvider.moduleList[NavRoutes.Detailed.route + "/$moduleID"]!!.mName,
                color = color_text_primary,
                maxLines = 1,

                )
        },


        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        }
    )
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

