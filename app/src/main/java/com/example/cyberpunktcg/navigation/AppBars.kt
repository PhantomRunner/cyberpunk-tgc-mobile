package com.example.cyberpunktcg.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.cyberpunktcg.R

private data class NavigationTab(
    val destination: AppDestination,
    @get:DrawableRes val iconResId: Int,
)

private val navigationTabs = listOf(
    NavigationTab(AppDestination.Home, R.drawable.ic_home),
    NavigationTab(AppDestination.Collection, R.drawable.ic_collection),
    NavigationTab(AppDestination.Decks, R.drawable.ic_decks),
    NavigationTab(AppDestination.Scanner, R.drawable.ic_scanner),
    NavigationTab(AppDestination.Settings, R.drawable.ic_settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppTopBar(
    @StringRes titleResId: Int,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(titleResId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = stringResource(R.string.back),
                    )
                }
            }
        },
    )
}

@Composable
internal fun AppBottomBar(
    selectedTab: AppDestination,
    onTabSelected: (AppDestination) -> Unit,
) {
    NavigationBar {
        navigationTabs.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab.destination,
                onClick = { onTabSelected(tab.destination) },
                icon = {

                    Icon(painterResource(tab.iconResId), contentDescription = null)
                },
                label = { Text(stringResource(tab.destination.titleResId)) },
            )
        }
    }
}
