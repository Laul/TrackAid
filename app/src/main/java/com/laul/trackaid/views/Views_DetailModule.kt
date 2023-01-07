package com.laul.trackaid

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import co.csadev.kellocharts.view.LineChartView
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compDetailedModule(navController: NavHostController){


    Scaffold(
        content = {innerPadding -> compDetailed(navController, innerPadding) },
        bottomBar = {BottomNavigationBar(navController)}
    )
}

@Composable
fun compDetailed(navController: NavHostController, innerPaddingValues: PaddingValues){
    Card() {
        Text(
            text ="Plop",
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.padding_mid))

            )
    }
}