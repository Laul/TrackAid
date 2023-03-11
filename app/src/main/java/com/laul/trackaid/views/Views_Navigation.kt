package com.laul.trackaid.views

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.laul.trackaid.R
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.theme.*


@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar(

//        containerColor = md_theme_light_surface,
//        contentColor = md_theme_light_onSurface
        modifier = Modifier
//            .background(Color(0x673C4F))
            .height(65.dp)
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

            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Green,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.Transparent,
                    indicatorColor = Color.White
                ),  
                alwaysShowLabel = true,
                selected =  selected,
//                selectedContentColor =md_theme_light_secondary,
//                unselectedContentColor = md_theme_light_primary,
//                modifier = Modifier.background(Color(0x673C4F)),
                label = {
                    Text(
                        text = item.value.mName,
                        fontSize = 10.sp
                    )
                },
                icon = {
                    Icon(
                        ImageVector.vectorResource(
                            id = item.value.mIcon
                        ),
                        tint = if (selected) md_theme_light_secondary else md_theme_light_primary,

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
                        restoreState = true
                    }

                })

        }
    }
}

