package com.laul.trackaid.views


class Navigation {
}

sealed class NavRoutes(     val route: String) {
    object Home     :   NavRoutes(route= "Home"    )
    object Detailed :   NavRoutes(route= "Detailed")
}

