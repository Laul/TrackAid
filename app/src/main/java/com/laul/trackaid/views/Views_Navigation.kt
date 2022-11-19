package com.laul.trackaid.views

import android.graphics.drawable.VectorDrawable
import androidx.compose.ui.res.vectorResource
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laul.trackaid.R
import com.laul.trackaid.ui.theme.backgroundColor
import com.laul.trackaid.ui.theme.grey_primary
import com.laul.trackaid.ui.theme.grey_secondary


@Preview(showBackground = true)
@Composable
fun BottomNarPreview() {
    BottomNavigationBar()
}

@Composable @Preview
fun FaB() {
    FloatingActionButton(
        onClick = { },
    ) {
        Icon(
            painterResource(R.drawable.ic_sync),
            contentDescription = "=",
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size))
        )
    }
}


@Composable @Preview
fun BottomNavigationBar() {
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(
        elevation = 15.dp,
        ) {

        BottomNavigationItem(icon = {
            Icon(ImageVector.vectorResource(
                id = R.drawable.ic_home),
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
        },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
            })

        BottomNavigationItem(icon = {
            Icon(ImageVector.vectorResource(
                id = R.drawable.ic_bg),
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
        },
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
            })

        BottomNavigationItem(icon = {
            Icon(ImageVector.vectorResource(
                id = R.drawable.ic_bp),
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
        },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
            })

        BottomNavigationItem(icon = {
            Icon(ImageVector.vectorResource(id = R.drawable.ic_steps),
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
        },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
            })

        BottomNavigationItem(icon = {
            Icon(ImageVector.vectorResource(id = R.drawable.ic_hr),
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)))
        },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
            })

    }
}