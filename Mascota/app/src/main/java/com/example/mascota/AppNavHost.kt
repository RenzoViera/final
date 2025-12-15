package com.example.mascota

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mascota.screens.common.LoginScreen
import com.example.mascota.screens.common.RegisterScreen
import com.example.mascota.screens.common.RoleSelectScreen
import com.example.mascota.screens.common.SplashScreen
import com.example.mascota.screens.client.ClientHomeScreen
import com.example.mascota.screens.client.pets.MyPetsScreen
import com.example.mascota.screens.client.pets.PetFormScreen
import com.example.mascota.screens.client.walks.*
import com.example.mascota.screens.walker.WalkerHomeScreen

// ✅ WALKER screens (imports)
import com.example.mascota.screens.walker.walks.WalkerWalksScreen
import com.example.mascota.screens.walker.walks.WalkerWalkDetailScreen
import com.example.mascota.screens.walker.reviews.ReviewsScreen
import com.example.mascota.screens.walker.reviews.ReviewDetailScreen

@Composable
fun AppNavHost(nav: NavHostController, container: AppContainer) {

    NavHost(
        navController = nav,
        startDestination = Routes.SPLASH
    ) {

        /* =======================
           COMMON
           ======================= */
        composable(Routes.SPLASH) { SplashScreen(nav, container) }
        composable(Routes.ROLE) { RoleSelectScreen(nav, container) }
        composable(Routes.LOGIN) { LoginScreen(nav, container) }
        composable(Routes.REGISTER) { RegisterScreen(nav, container) }

        /* =======================
           CLIENT (DUEÑO)
           ======================= */
        composable(Routes.CLIENT_HOME) {
            ClientHomeScreen(nav, container)
        }

        composable(Routes.CLIENT_PETS) {
            MyPetsScreen(nav, container)
        }

        /* ---------- PASEOS CLIENTE ---------- */
        composable(Routes.CLIENT_WALKS) {
            WalksScreen(nav, container)
        }

        composable(
            route = "${Routes.CLIENT_WALK_DETAIL}/{walkId}",
            arguments = listOf(
                navArgument("walkId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val walkId = backStackEntry.arguments?.getLong("walkId") ?: 0L
            WalkDetailScreen(nav, container, walkId)
        }

        /* ---------- NUEVO PASEO CLIENTE ---------- */

        composable(Routes.CLIENT_CHOOSE_ADDRESS) {
            ChooseAddressScreen(nav, container)
        }

        composable(
            route = "${Routes.CLIENT_WALKERS_NEARBY}/{addressId}",
            arguments = listOf(
                navArgument("addressId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getLong("addressId") ?: 0L
            NearbyWalkersScreen(nav, container, addressId)
        }

        composable(
            route = "${Routes.CLIENT_WALKER_DETAIL}/{walkerId}?addressId={addressId}&walkId={walkId}",
            arguments = listOf(
                navArgument("walkerId") { type = NavType.LongType },
                navArgument("addressId") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("walkId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val walkerId = backStackEntry.arguments?.getLong("walkerId") ?: 0L
            val addressId = backStackEntry.arguments?.getLong("addressId") ?: 0L
            val walkId = backStackEntry.arguments?.getLong("walkId") ?: 0L

            WalkerDetailScreen(nav, container, walkerId, addressId, walkId)
        }


        composable(
            route = "${Routes.CLIENT_WALK_REQUEST}/{walkerId}?addressId={addressId}",
            arguments = listOf(
                navArgument("walkerId") { type = NavType.LongType },
                navArgument("addressId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val walkerId = backStackEntry.arguments?.getLong("walkerId") ?: 0L
            val addressId = backStackEntry.arguments?.getLong("addressId") ?: 0L
            RequestWalkScreen(nav, container, walkerId, addressId)
        }

        /* ---------- MAPA CLIENTE (solo EN CURSO) ---------- */
        composable(
            route = "${Routes.CLIENT_WALK_MAP}/{walkId}",
            arguments = listOf(navArgument("walkId") { type = NavType.LongType })
        ) { backStackEntry ->
            val walkId = backStackEntry.arguments?.getLong("walkId") ?: 0L
            WalkMapScreen(nav, container, walkId)
        }

        /* ---------- REVIEW CLIENTE (solo FINALIZADO) ---------- */
        composable(
            route = "${Routes.CLIENT_WALK_REVIEW}/{walkId}",
            arguments = listOf(navArgument("walkId") { type = NavType.LongType })
        ) { backStackEntry ->
            val walkId = backStackEntry.arguments?.getLong("walkId") ?: 0L
            WalkReviewScreen(nav, container, walkId)
        }

        /* =======================
           PET FORM
           ======================= */
        composable(
            route = Routes.CLIENT_PET_FORM + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString("id")
                ?.toLongOrNull()

            PetFormScreen(
                nav = nav,
                container = container,
                petId = id
            )
        }

        /* =======================
           WALKER
           ======================= */

        composable(Routes.WALKER_HOME) {
            WalkerHomeScreen(nav, container)
        }

        // Mis paseos (tabs Pendientes/Aceptados, etc.)
        composable(Routes.WALKER_WALKS) {
            WalkerWalksScreen(nav, container)
        }

        // Detalle paseo (paseador)
        composable(
            route = "${Routes.WALKER_WALK_DETAIL}/{walkId}",
            arguments = listOf(
                navArgument("walkId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val walkId = backStackEntry.arguments?.getLong("walkId") ?: 0L
            WalkerWalkDetailScreen(nav, container, walkId)
        }

        // Mis reviews
        composable(Routes.WALKER_REVIEWS) {
            ReviewsScreen(nav, container)
        }

        // Detalle review
        composable(
            route = "${Routes.WALKER_REVIEW_DETAIL}/{reviewId}",
            arguments = listOf(
                navArgument("reviewId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getLong("reviewId") ?: 0L
            ReviewDetailScreen(nav, container, reviewId)
        }
    }
}
