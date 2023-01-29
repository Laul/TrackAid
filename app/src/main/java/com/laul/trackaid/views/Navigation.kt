package com.laul.trackaid.views

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.laul.trackaid.R
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.DataProvider.moduleList
import com.laul.trackaid.data.ModuleData

class Navigation {
}

sealed class NavRoutes(     val route: String) {
    object Home     :   NavRoutes(route= "Home"    )
    object Detailed :   NavRoutes(route= "Detailed")
}

