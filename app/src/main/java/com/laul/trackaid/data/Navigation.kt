package com.laul.trackaid.data

sealed class NavRoutes(     val route: String) {
    object Home     :   NavRoutes(route= "Home"     )
    object Detailed :   NavRoutes(route= "Detailed" )
    object Drugs    :   NavRoutes(route = "Drugs"   )
}

