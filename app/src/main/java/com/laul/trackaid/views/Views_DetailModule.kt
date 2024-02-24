package com.laul.trackaid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laul.trackaid.data.DataProvider
import com.laul.trackaid.data.NavRoutes
import com.laul.trackaid.theme.color_general_primary
import com.laul.trackaid.theme.color_general_white
import com.laul.trackaid.theme.color_surface_background
import com.laul.trackaid.theme.color_text_secondary
import com.laul.trackaid.views.BottomNavigationBar
import com.laul.trackaid.views.TopNavigationBar


/** Structure of the Detailed views. Common to all modules
 * @param navController: Host controller for navigation
 * @param moduleID: Id of the module as string to retrieve proper data
 */
@Composable
fun compDetailedModule(navController: NavHostController, moduleID : String?){
    Scaffold(
        containerColor = color_surface_background,
        topBar = { TopNavigationBar(navController, moduleID) },
        content = {innerPadding -> compDetailed(moduleID) },
        bottomBar = {BottomNavigationBar(navController)}
    )
}


/** Specific content of the detailed view
 * @param moduleID: Id of the module as string to retrieve proper data
 */
@Composable
fun compDetailed(moduleID : String?){
    var module =  DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!!
    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 90.dp,
            )

    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 25.dp)
                .height(110.dp)
        ) {
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

        Box (
            modifier = Modifier
                .fillMaxWidth() // Take the full width
                .height(220.dp) // Set the height of the box
//                .background(Color(module.mColor_Primary!!)) // Set background color of the box
                .padding(bottom = 1.dp) // Add padding to the bottom to create the border
                .background(Color.White) // Set background color of the border

        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(color_general_white)
                    .padding(top =0.dp, bottom = 10.dp, start = 5.dp, end = 5.dp)


            ) {
                compChart(
                    module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!!,
                    isDetailedView = true,
                )

            }

        }

        compChart_Detailed(
            module = DataProvider.moduleList[NavRoutes.Detailed.route + "/" + moduleID]!!
        )

    }
}



