package com.laul.trackaid.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.laul.trackaid.R
import com.laul.trackaid.connection.BloodGlucoseUpdate
import com.laul.trackaid.data.DataGeneral
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
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
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 56.dp)
                    )
                },
                icon = {
                    Icon(
                        ImageVector.vectorResource(
                            id = if (selected) item.value.mIcon else item.value.mIcon_outlined
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium))
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

