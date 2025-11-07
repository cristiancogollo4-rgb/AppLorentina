package com.cristiancogollo.applorentina

import AgregarClienteScreen
import StockScreen
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import me.oscarsanchez.myapplication.NventaDialogScreen

sealed class Screen(val route: String) {
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

    // Rutas del Home del Administrador
    object AdminProduccion : Screen("admin_produccion_screen")
    object AdminClientes : Screen("admin_clientes_screen")
    object AdminInventario : Screen("admin_inventario_screen")
    object AdminVentas : Screen("admin_ventas_screen")
}

@Composable
fun NavigationApp() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // ✅ Detectar usuario actual
    val currentUser = auth.currentUser
    val startDestination = remember { mutableStateOf(Screen.Login.route) }

    // ✅ Si hay sesión activa, decidir el rol según el dominio del correo
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val email = currentUser.email ?: ""
            startDestination.value = when {
                email.endsWith("@admin.com") -> Screen.HomeAdmin.route
                email.endsWith("@gmail.com") || email.endsWith("@hotmail.com") -> Screen.HomeVendedor.route
                else -> Screen.Login.route
            }
        } else {
            startDestination.value = Screen.Login.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination.value
    ) {
        // ============================================================
        // LOGIN
        // ============================================================
        composable(Screen.Login.route) {
            LorentinaLoginScreen(
                onLoginClick = { selectedRole ->
                    when (selectedRole) {
                        UserRole.VENDEDOR -> navController.navigate(Screen.HomeVendedor.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        UserRole.ADMINISTRADOR -> navController.navigate(Screen.HomeAdmin.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onForgotPasswordClick = { /* acción futura */ }
            )
        }

        // ============================================================
        // HOME VENDEDOR
        // ============================================================
        composable(Screen.HomeVendedor.route) {
            HomeScreen(
                navController = navController,
                onLogoutClick = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.HomeVendedor.route) { inclusive = true }
                    }
                }
            )
        }

        // ============================================================
        // HOME ADMINISTRADOR
        // ============================================================
        composable(Screen.HomeAdmin.route) {
            HomeAdmin(
                onLogoutClick = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.HomeAdmin.route) { inclusive = true }
                    }
                },
                navTo = { route -> navController.navigate(route) }
            )
        }

        // ============================================================
        // SUBPANTALLAS DEL VENDEDOR
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
        // SUBPANTALLAS DEL ADMINISTRADOR
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
