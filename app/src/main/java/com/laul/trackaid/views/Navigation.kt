package com.laul.trackaid.views

import android.graphics.drawable.Icon
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.DataProvider.moduleList
import com.laul.trackaid.data.ModuleData

class Navigation {
}

sealed class NavRoutes(val route: String) {
    object Home     :   NavRoutes(route= "Home"  )
    object Detailed :   NavRoutes(route= "Detailed")
}


// Screen routes ____________________________
//
//sealed class NavRoutes(val route: String , val dataModule : ModuleData) {
//    object Home : NavRoutes(
//        route= "Home",
//        dataModule= dataModule.
//    )
//    object Detailed : NavRoutes("Detailed")
//}

//
//
//sealed class NavRoutes(val route: String, val icon: Icon, val title: String) {
//    object Home : NavRoutes(
//        route= "Home",
//        icon=
//    )
//    object Detailed : NavRoutes("Detailed")
//}
