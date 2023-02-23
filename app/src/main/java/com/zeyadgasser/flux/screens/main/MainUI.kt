package com.zeyadgasser.flux.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zeyadgasser.flux.ui.theme.FluxTheme
import com.zeyadgasser.mvi.MVIScreen
import com.zeyadgasser.mvvm.MVVMScreen

@Composable
fun HomeScreen() {
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { HomeScreenTopBar() },
    ) { paddingValues ->
        val navController = rememberNavController()
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colors.background
        ) { FluxNavHost(navController = navController) }
    }
}

@Composable
fun FluxNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) = NavHost(
    navController = navController,
    startDestination = Main.route,
    modifier = modifier,
) {
    composable(Main.route) {
        HomeScreenContent(
            { navController.navigateSingleTopTo(MVI.route) },
            { navController.navigateSingleTopTo(MVVM.route) }
        )
    }
    composable(MVI.route) { MVIScreen { navController.navigateSingleTopTo(Main.route) } }
    composable(MVVM.route) { MVVMScreen { navController.navigateSingleTopTo(Main.route) } }
}

@Composable
fun HomeScreenContent(mviOnClick: () -> Unit, mvvmOnClick: () -> Unit) =
    Column(Modifier.padding(PaddingValues(Dp(28f)))) {
        Button(onClick = mviOnClick) { Text(text = "MVI Sample") }
        Button(onClick = mvvmOnClick) { Text(text = "MVVM Sample") }
    }

@Composable
fun HomeScreenTopBar() =
    TopAppBar(Modifier.fillMaxWidth()) { Text("Flux Samples", Modifier.padding(8.dp)) }

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() = FluxTheme { HomeScreen() }