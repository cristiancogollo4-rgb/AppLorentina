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
    object HomeAdmin : Screen("home_admin_screen")

    // Rutas del Home Vendedor
    object Estadisticas : Screen("estadisticas_screen")
    object BuscarCliente : Screen("buscar_cliente_screen")
    object AgregarCliente : Screen("agregar_cliente_screen")
    object Produccion : Screen("produccion_screen")
    object Stock : Screen("stock_screen")
    object Hventas : Screen("hventas_screen")
    object Nventa : Screen("nventa_screen")

    // 🔹 NUEVAS rutas del Home del Administrador
    object AdminProduccion : Screen("admin_produccion_screen")
    object AdminClientes : Screen("admin_clientes_screen")
    object AdminInventario : Screen("admin_inventario_screen")
    object AdminVentas : Screen("admin_ventas_screen")
}

@Composable
fun NavigationApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // ============================================================
        // 1️⃣ LOGIN
        // ============================================================
        composable(Screen.Login.route) {
            LorentinaLoginScreen(
                onLoginClick = { selectedRole ->
                    when (selectedRole) {
                        UserRole.VENDEDOR -> navController.navigate(Screen.HomeVendedor.route)
                        UserRole.ADMINISTRADOR -> navController.navigate(Screen.HomeAdmin.route)
                    }
                },
                onForgotPasswordClick = { /* Acción futura */ }
            )
        }

        // ============================================================
        // 2️⃣ HOME VENDEDOR
        // ============================================================
        composable(Screen.HomeVendedor.route) {
            HomeScreen(
                navController = navController,
                onLogoutClick = { navController.popBackStack() }
            )
        }

        // ============================================================
        // 3️⃣ HOME ADMINISTRADOR
        // Con navegación interna a sus secciones
        // ============================================================
        composable(Screen.HomeAdmin.route) {
            HomeAdmin(
                onLogoutClick = { navController.popBackStack() },
                navTo = { route -> navController.navigate(route) } // 👈 nuevo parámetro
            )
        }

        // ============================================================
        // 4️⃣ SUBPANTALLAS DEL VENDEDOR
        // ============================================================
        composable(Screen.Estadisticas.route) {
            EstadisticasScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.BuscarCliente.route) {
            BuscarClienteScreen(
                onBackClick = { navController.popBackStack() },
                onAddClientClick = { navController.navigate(Screen.AgregarCliente.route) }
            )
        }

        dialog(Screen.AgregarCliente.route) {
            AgregarClienteScreen()
        }

        composable(Screen.Produccion.route) {
            ProduccionScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Stock.route) {
            StockScreen(onBackClick = { navController.popBackStack() })
        }

        dialog(Screen.Nventa.route) {
            NventaDialogScreen()
        }

        composable(Screen.Hventas.route) {
            HventasScreen(
                onBackClick = { navController.popBackStack() },
                onNewVentaClick = { navController.navigate(Screen.Nventa.route) }
            )
        }

        // ============================================================
        // 5️⃣ SUBPANTALLAS DEL ADMINISTRADOR
        // ============================================================

        composable(Screen.AdminProduccion.route) {
            ProduccionScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AdminClientes.route) {
            BuscarClienteScreen(
                onBackClick = { navController.popBackStack() },
                onAddClientClick = { navController.navigate(Screen.AgregarCliente.route) }
            )
        }

        composable(Screen.AdminInventario.route) {
            StockScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AdminVentas.route) {
            HventasScreen(
                onBackClick = { navController.popBackStack() },
                onNewVentaClick = { navController.navigate(Screen.Nventa.route) }
            )
        }
    }
}
