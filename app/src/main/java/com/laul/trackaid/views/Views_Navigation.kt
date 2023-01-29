package com.laul.trackaid.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigationItem

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.laul.trackaid.R
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.theme.md_theme_light_onSurfaceVariant
import com.laul.trackaid.theme.md_theme_light_secondaryContainer


@Composable
fun BottomNavigationBar(navController: NavController) {

    val selectedIndex = remember { mutableStateOf(0) }
//
//    val items = listOf(
//        NavRoutes.Home.route,
//        NavRoutes.Detailed.route + "/1",
//        NavRoutes.Detailed.route + "/2",
//        NavRoutes.Detailed.route + "/3",
//        NavRoutes.Detailed.route + "/4"
//    )


    NavigationBar(
        ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val detailedID = navBackStackEntry?.arguments?.getString("moduleID")

        DataProvider.moduleList.forEach { item ->
            var selected = false
            if (currentRoute != null) {
                if (currentRoute.startsWith("Detailed") && item.key.startsWith("Detailed")){
                    if (detailedID != null) {
                        selected = item.key.endsWith(detailedID)
                    }
                }
                else {
                    selected = currentRoute == item.key // if current route equal to screen route it return true
                }
            }

            BottomNavigationItem(
                label = {
                    Text(text = item.value.mName,
                    fontSize = 9.sp)
                },
                icon = {
                    Icon(
                        ImageVector.vectorResource(
                            id = item.value.mIcon
                        ),
                        tint = if (selected) Color.White else Color.Black,

                        contentDescription = "",
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                    )
                },
                selected =  selected,
                selectedContentColor =Color(R.color.red_primary) ,
                unselectedContentColor = md_theme_light_onSurfaceVariant,
                onClick = {
                    navController.navigate(item.key) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = false
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                })

        }
    }
}