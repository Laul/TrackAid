package com.laul.trackaid.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.fhir.get
import com.laul.trackaid.connection.FhirApplication.Companion.fhirEngine
import com.laul.trackaid.theme.color_general_primary
import com.laul.trackaid.theme.color_surface_background
import com.laul.trackaid.theme.color_text_primary
import com.laul.trackaid.theme.color_text_secondary
import com.laul.trackaid.theme.md_theme_light_secondaryContainer
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient

@Composable
    fun compDrugsModule(navController: NavController) {
        Scaffold(
            containerColor = color_surface_background,
            topBar = { TopNavigationBar(navController, "Drugs") },
            content = {innerPadding -> compQuestDrugs() },
            bottomBar = {BottomNavigationBar(navController)}
        )

    }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun compQuestDrugs(){
    var dName by remember { mutableStateOf("") }
    var dQuantity by remember { mutableStateOf("") }

    // Instantiate FHIR engine
    // TODO
//    var fhirEngine = fhirEngine(LocalContext.current)
//    val fhirCoroutineScope = rememberCoroutineScope()
//
//
//    // val patient = entry.resource as Patient
//
//    fhirCoroutineScope.launch {
//        val patient = fhirEngine.get<Patient>("1")
//        fhirEngine.create(patient)
//    }

    Column (
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp) ,
        modifier = Modifier
            .padding(start = 20.dp, top = 80.dp, end = 20.dp)
            .fillMaxWidth()
   )
    {
        Text (
            text = "Please, enter the details below." ,
            style = typography.bodyMedium,
            color = color_text_primary
        )

        tTextField(dName, "eg. Statex", "Enter drug name")

        Row (
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)

        )
        {
            Box(Modifier.weight(5f))
            { tTextField(dName, "eg. 3", "Enter drug quantity") }

            Box(Modifier.weight(2f))
            {tDropDown(arrayOf("mg", "mL"))}

        }

        OutlinedIconButton(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp), // Set corner radius to 2dp
            colors =  IconButtonDefaults.outlinedIconButtonColors(
                containerColor = md_theme_light_secondaryContainer,
                contentColor = color_text_secondary
            ),
            border = BorderStroke(1.dp , color_general_primary),

            onClick = { /* Handle button click here */ }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        53.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Save",
                        color = color_text_primary,
                        textAlign = TextAlign.Center,
                        style = typography.titleSmall,
                        modifier = Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tDropDown( menuItems: Array<String>) {

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(menuItems[0]) }
    var context = LocalContext.current


    ExposedDropdownMenuBox(

            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            // tTextField (selectedText, "unit", "")
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                maxLines= 1,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .height(45.dp)
                    .menuAnchor()
                    .background(color_surface_background),
                textStyle = typography.bodyMedium
            )

            ExposedDropdownMenu(
                modifier = Modifier.background(color_surface_background),

                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItems.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                style = typography.bodyMedium,
                            )
                        },
                        onClick = {
                            selectedText = item
                            expanded = false
                            Toast.makeText(context, item, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tTextField(value: String, label: String, supportText: String ) {
    var value = value

    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = { value = it },

        interactionSource = interactionSource,
        enabled = true,
        singleLine = true,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
    ) { innerTextField ->
        TextFieldDefaults.OutlinedTextFieldDecorationBox(
            value =value,

            singleLine = true,
            enabled = true,
            innerTextField = innerTextField,
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            contentPadding = PaddingValues(10.dp), // this is how you can remove the padding

            label = {
                Text(
                    text = label,
                    style = typography.bodyMedium,
                )
            },
            supportingText = { Text(supportText)}
        )
    }
}

