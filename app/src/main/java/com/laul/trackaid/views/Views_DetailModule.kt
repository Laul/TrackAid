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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import co.csadev.kellocharts.view.LineChartView
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.ModuleData
import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compDetailedModule(navController: NavHostController, moduleID : String?){

    Scaffold(
        content = {innerPadding -> compDetailed(navController, innerPadding, moduleID) },
        bottomBar = {BottomNavigationBar(navController)}
    )
}

@Composable
fun compDetailed(navController: NavHostController, innerPaddingValues: PaddingValues, moduleID : String?){
    var ctx = LocalContext.current
    Text(
        text ="Plop" + moduleID.toString(),
        modifier = Modifier
            .padding(start = dimensionResource(R.dimen.padding_mid))

    )
    compChart_Detailed(context = ctx, module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!! )

//    Card() {
//        Text(
//            text ="Plop" + moduleID.toString(),
//            modifier = Modifier
//                .padding(start = dimensionResource(R.dimen.padding_mid))
//
//            )
//        compChart(context = ctx, module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!! )
//
//        AndroidView(
//
//            modifier = Modifier.size(200.dp),
//            factory = { ctx: Context ->
//
//
//                //  Initialize a View or View hierarchy here
//                LineChartView(ctx).apply {
//                    lineChartData = module.kChart_Data
//                }
//
//            },
//            update = {
//                it.lineChartData = module.kChart_Data
////                        module.kYAxis = Axis(hasLines = true, maxLabels = 4)
////                        module.kYAxis.name = lastCall.value.toString()
//            }
//
//        )

//    }
}
