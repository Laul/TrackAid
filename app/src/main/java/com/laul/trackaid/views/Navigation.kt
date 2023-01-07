package com.laul.trackaid.views

class Navigation {
}


// Screen routes ____________________________

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("Home")
    object Detailed : NavRoutes("Detailed")
}
