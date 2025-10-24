package com.example.my_calculator

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.my_calculator.screens.*

@Composable
fun NavGraph(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it / 3 }) },
        popEnterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it / 3 }) },
        popExitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it }) }
    ) {
        composable("home") { HomeScreen(navController) }
        composable("car_loan") { CarLoanScreen(navController) }
        composable("housing_loan") { HousingLoanScreen(navController) }
        composable("dsr") { DSRScreen(navController) }
        composable("fd") { FDScreen(navController) }
        composable("legal_fees") { LegalFeesScreen(navController) }
        composable("late_payment") { LatePaymentScreen(navController) }
        composable("rpgt") { RPGTScreen(navController) }
        composable("general_calculator") { GeneralCalculatorScreen(navController) }
        composable("selling_price_calculator") { SellingPriceCalculatorScreen(navController) }
    }
}
