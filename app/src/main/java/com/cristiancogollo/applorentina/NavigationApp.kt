package com.cristiancogollo.applorentina

import StockScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import me.oscarsanchez.myapplication.NventaDialogScreen

/**
 * 🗺️ Rutas de Navegación
 * Define todas las pantallas a las que se puede navegar.
 */
sealed class Screen(val route: String) {
    // Rutas principales
    object Login : Screen("login_screen")
    object HomeVendedor : Screen("home_vendedor_screen")
    object HomeAdmin : Screen("home_admin_screen") // Todavía no implementada su navegación interna

    // Rutas de Home Vendedor
    object Estadisticas : Screen("estadisticas_screen")
    object BuscarCliente : Screen("buscar_cliente_screen")
    object AgregarCliente : Screen("agregar_cliente_screen")
    object Produccion : Screen("produccion_screen")
    object Stock : Screen("stock_screen")
    object Hventas : Screen("hventas_screen")
    object Nventa : Screen("nventa_screen")
}

@Composable
fun NavigationApp() {
    // Controlador de navegación principal
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // ====================================================================
        // 1. LOGIN
        // La pantalla inicial. Navega a HomeVendedor o HomeAdmin
        // ====================================================================
        composable(Screen.Login.route) {
            LorentinaLoginScreen(
                onLoginClick = { selectedRole ->
                    // Decide a qué Home ir según el rol
                    when (selectedRole) {
                        UserRole.VENDEDOR -> navController.navigate(Screen.HomeVendedor.route)
                        UserRole.ADMINISTRADOR -> navController.navigate(Screen.HomeAdmin.route)
                    }
                },
                onForgotPasswordClick = { /* Acción para "Contraseña Olvidada" */ }
            )
        }

        // ====================================================================
        // 2. HOME VENDEDOR
        // Recibe el navController para navegar a sus 5 subpantallas.
        // Recibe onLogoutClick para volver al Login.
        // ====================================================================
        composable(Screen.HomeVendedor.route) {
            HomeScreen(
                navController = navController, // <-- Permite la navegación interna
                onLogoutClick = { navController.popBackStack() } // <-- Vuelve a la pantalla anterior (Login)
            )
        }

        // ====================================================================
        // 3. HOME ADMINISTRADOR
        // Solo implementada la acción de cerrar sesión por ahora.
        // ====================================================================
        composable(Screen.HomeAdmin.route) {
            HomeAdmin(
                onLogoutClick = { navController.popBackStack() } // <-- Vuelve a la pantalla anterior (Login)
            )
        }

        // ====================================================================
        // 4. SUBPANTALLAS DE HOME VENDEDOR
        // ====================================================================

        composable(Screen.Estadisticas.route) {
            EstadisticasScreen(
                onBackClick = { navController.popBackStack() } // Lógica para volver
            )
        }

        composable(Screen.BuscarCliente.route) {
            BuscarClienteScreen(
                onBackClick = { navController.popBackStack() },
                onAddClientClick = {
                    navController.navigate(Screen.AgregarCliente.route)
                }
            )
        }
        dialog(Screen.AgregarCliente.route) {
            AgregarClienteScreen()
        }

        composable(Screen.Produccion.route) {
            ProduccionScreen(
                onBackClick = { navController.popBackStack() } //  Lógica para volver
            )
        }

        composable(Screen.Stock.route) {
            StockScreen(
                onBackClick = { navController.popBackStack() } //  Lógica para volver
            )
        }
        dialog(Screen.Nventa.route) {
            NventaDialogScreen()
        }

        composable(Screen.Hventas.route) {
            HventasScreen(
                onBackClick = { navController.popBackStack() },// 👈 Lógica para volver
                onNewVentaClick = { navController.navigate(Screen.Nventa.route) }

            )
        }
    }
}
