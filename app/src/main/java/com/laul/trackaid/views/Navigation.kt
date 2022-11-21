package com.laul.trackaid.views

class Navigation {
}


// Screen routes ____________________________

sealed class Screens(val route: String) {
    object Home : Screens("Home")
    object Module : Screens("Detailed")
}