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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.laul.trackaid.R
import com.laul.trackaid.theme.md_theme_light_onSurfaceVariant
import com.laul.trackaid.theme.md_theme_light_secondaryContainer


//
//@Preview(showBackground = true)
//@Composable
//fun BottomNavPreview() {
//    BottomNavigationBar(navController)
//}

//@Composable @Preview
//fun FaB() {
//    FloatingActionButton(
//        onClick = { },
//    ) {
//        Icon(
//            painterResource(R.drawable.ic_sync),
//            contentDescription = "=",
//            modifier = Modifier.size(dimensionResource(R.dimen.icon_size))
//        )
//    }
//}

//
//@Composable
//fun BottomNavigationBar(
//    modifier: Modifier = Modifier,
//    containerColor: Color = BottomAppBarDefaults.containerColor,
//    contentColor: Color = contentColorFor(containerColor),
//    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
//    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
//    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
//    content: @Composable RowScope.() -> Unit
//)
//
//data class BarItem(
//    val title: String,
//)
//
@Composable
fun BottomNavigationBar(navController: NavController) {






//
    val selectedIndex = remember { mutableStateOf(0) }
//
//    NavigationBar () {
//        IconButton(
//            onClick = {
//                selectedIndex.value = 1
//                Log.i("Selected index: ", selectedIndex.value.toString())
//            }) {
//            Icon(ImageVector.vectorResource(
//                id = R.drawable.ic_home),
//                contentDescription = "",
//                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
//        }
//        IconButton(            onClick = {
//            selectedIndex.value = 2
//            Log.i("Selected index: ", selectedIndex.value.toString())
//        }) {
//            Icon(ImageVector.vectorResource(
//                id = R.drawable.ic_bg),
//                contentDescription = "",
//                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
//        }
//        IconButton(            onClick = {
//            selectedIndex.value = 3
//            Log.i("Selected index: ", selectedIndex.value.toString())
//        }) {
//            Icon(ImageVector.vectorResource(
//                id = R.drawable.ic_steps),
//                contentDescription = "",
//                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
//        }
//        IconButton(            onClick = {
//            selectedIndex.value = 4
//            Log.i("Selected index: ", selectedIndex.value.toString())
//        }) {
//            Icon(ImageVector.vectorResource(
//                id = R.drawable.ic_hr),
//                contentDescription = "",
//                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
//        }
//
//        IconButton(            onClick = {
//            selectedIndex.value = 5
//            Log.i("Selected index: ", selectedIndex.value.toString())
//        }) {
//            Icon(ImageVector.vectorResource(
//                id = R.drawable.ic_bp),
//                contentDescription = "",
//                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
//        }
//
//        // The actions should be at the end of the BottomAppBar
//        Spacer(Modifier.weight(1f, true))
//
////        floatingActionButton = { FaB() }
//
//        IconButton(onClick = { selectedIndex.value = 6}) {
//            Icon(ImageVector.vectorResource(
//                id = R.drawable.ic_sync),
//                contentDescription = "",
//                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
//        }
//    }


    val items = listOf(
        NavRoutes.Home,
        NavRoutes.Detailed,
    )

    NavigationBar(
        ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route


        items.forEach { item ->

            val selected = currentRoute == item.route // if current route equal to screen route it return true


            BottomNavigationItem(
                label = {
                    Text(text = "plpo",
                    fontSize = 9.sp)
                },
                icon = {
                    Icon(
                        ImageVector.vectorResource(
                            id = R.drawable.ic_home
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
                    navController.navigate(item.route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                })

        }
    }
}