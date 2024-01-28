package com.laul.trackaid

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.NavRoutes
import com.laul.trackaid.theme.color_general_primary
import com.laul.trackaid.theme.color_surface_background
import com.laul.trackaid.theme.color_text_secondary
import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.TopNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compDetailedModule(navController: NavHostController, moduleID : String?){
    Scaffold(
        containerColor = color_surface_background,
        topBar = { TopNavigationBar(navController, moduleID) },
        content = {innerPadding -> compDetailed(navController, innerPadding, moduleID) },
        bottomBar = {BottomNavigationBar(navController)}
    )
}

@Composable
fun compDetailed(navController: NavHostController, innerPaddingValues: PaddingValues, moduleID : String?){
    var module =  DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!!
    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 80.dp,
                horizontal = dimensionResource(id = R.dimen.padding_large)
            )

    ) {
        Row() {
            // Last measure information
            Column(
                modifier = Modifier.weight(3f)
            )
            {
                Text(
                    text = "Last measure",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = module.lastDPoint!!.value.date.toString(),
                    color = color_text_secondary,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                )

                Text(
                    if (module.mName == "Glucose") {
                        "%.2f".format(module.lastDPoint!!.value.value)
                    }
                    //else if (module.mName == "Pressure") {
//                        "%.0f-%.0f".format(lastDPoint!!.value.value[1], lastDPoint!!.value.value[0])
                    //    }
                    else {
                        "%.0f".format(module.lastDPoint!!.value.value)
                    },
                    color = color_general_primary,
                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                )
            }

            Column(
                modifier = Modifier.weight(4f)
            )
            {
                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = "Analytics",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                )
                Row (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Average
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            text = "Average",
                            color = color_text_secondary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        )

                        Text(
                            if (module.mName == "Glucose") {
                                "%.2f".format(module.stats!!.value.avg)
                            }
                            //else if (module.mName == "Pressure") {
//                        "%.0f-%.0f".format(lastDPoint!!.value.value[1], lastDPoint!!.value.value[0])
                            //    }
                            else {
                                "%.0f".format(module.stats!!.value.avg)
                            },
                            color = color_general_primary,
                            style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                        )
                    }

                    // Min
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Min",
                            color = color_text_secondary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        )

                        Text(
                            if (module.mName == "Glucose") {
                                "%.2f".format(module.stats!!.value.min)
                            }
                            //else if (module.mName == "Pressure") {
//                        "%.0f-%.0f".format(lastDPoint!!.value.value[1], lastDPoint!!.value.value[0])
                            //    }
                            else {
                                "%.0f".format(module.stats!!.value.min)
                            },
                            color = color_general_primary,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                        )
                    }

                    // Max
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Max",
                            color = color_text_secondary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        )

                        Text(
                            if (module.mName == "Glucose") {
                                "%.2f".format(module.stats!!.value.max)
                            }
                            //else if (module.mName == "Pressure") {
//                        "%.0f-%.0f".format(lastDPoint!!.value.value[1], lastDPoint!!.value.value[0])
                            //    }
                            else {
                                "%.0f".format(module.stats!!.value.max)
                            },
                            color = color_general_primary,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                        )
                    }
                }

            }




        }

        compChart(
            module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!!,
            isDetailedView = true,
            backgroundColor = color_surface_background
        )



        compChart_Detailed(
            module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!!,
            backgroundColor = color_surface_background,
        )
    }
}



