package com.cristiancogollo.applorentina

import StockScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.oscarsanchez.myapplication.NventaDialogScreen

sealed class Screen(val route: String) {

    // Rutas principales
    object Login : Screen("login_screen")
    object HomeVendedor : Screen("home_vendedor_screen")
    object HomeAdmin : Screen("home_admin_screen")

    // Rutas del vendedor
    object Estadisticas : Screen("estadisticas_screen")
    object BuscarCliente : Screen("buscar_cliente_screen")
    object AgregarCliente : Screen("agregar_cliente_screen")
    object Produccion : Screen("produccion_screen")
    object Stock : Screen("stock_screen")
    object Hventas : Screen("hventas_screen")
    object Nventa : Screen("nventa_screen")
    object DetallesCliente : Screen("detalle_cliente_screen/{nombre}/{cedula}/{telefono}/{correo}/{departamento}/{municipio}/{tipoCliente}")
// ...


    // Rutas del administrador
    object ProduccionAdmin : Screen("produccion_admin_screen")
    object ClientesAdmin : Screen("clientes_admin_screen")
    object InventarioAdmin : Screen("inventario_admin_screen")
    object EstadisticasAdmin : Screen("estadisticas_admin_screen")
    object AgregarTareaAdmin : Screen("agregar_tarea_admin_screen")
}

@Composable
fun NavigationApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // 🟩 LOGIN
        composable(Screen.Login.route) {
            LorentinaLoginScreen(
                onLoginClick = { selectedRole ->
                    when (selectedRole) {
                        UserRole.VENDEDOR -> navController.navigate(Screen.HomeVendedor.route)
                        UserRole.ADMINISTRADOR -> navController.navigate(Screen.HomeAdmin.route)
                    }
                },
                onForgotPasswordClick = { }
            )
        }

        // 🟦 HOME VENDEDOR
        composable(Screen.HomeVendedor.route) {
            HomeScreen(
                navController = navController,
                onLogoutClick = { navController.popBackStack() }
            )
        }

        // 🟨 HOME ADMIN
        composable(Screen.HomeAdmin.route) {
            HomeAdmin(
                onLogoutClick = { navController.popBackStack() },
                navTo = { route -> navController.navigate(route) }
            )
        }

        // 🟧 SUBPANTALLAS DEL VENDEDOR
        composable(Screen.Estadisticas.route) {
            EstadisticasScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.BuscarCliente.route) {
            BuscarClienteScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onAddClientClick = { navController.navigate(Screen.AgregarCliente.route) }
            )
        }

        // 🔹 Pantalla de DetalleCliente


        dialog(Screen.AgregarCliente.route) { AgregarClienteScreen() }

        composable(Screen.Produccion.route) {
            ProduccionScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Stock.route) {
            StockScreen(onBackClick = { navController.popBackStack() })
        }

        dialog(Screen.Nventa.route) { NventaDialogScreen() }
        // ...
        dialog(
            route = Screen.DetallesCliente.route,
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("cedula") { type = NavType.StringType },
                navArgument("telefono") { type = NavType.StringType },
                navArgument("correo") { type = NavType.StringType },
                navArgument("departamento") { type = NavType.StringType },
                navArgument("municipio") { type = NavType.StringType },
                navArgument("tipoCliente") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Obtenemos los argumentos del backStackEntry
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Sin Nombre"
            val cedula = backStackEntry.arguments?.getString("cedula") ?: "Sin Cédula"
            val telefono = backStackEntry.arguments?.getString("telefono") ?: "Sin Teléfono"
            val correo = backStackEntry.arguments?.getString("correo") ?: "Sin Correo"
            val departamento = backStackEntry.arguments?.getString("departamento") ?: "Sin Departamento"
            val municipio = backStackEntry.arguments?.getString("municipio") ?: "Sin Municipio"
            val tipoCliente = backStackEntry.arguments?.getString("tipoCliente") ?: "false"

            // Llamamos al diálogo pasándole los datos que extrajimos
            DetalleClienteDialog(
                navController = navController,
                nombre = nombre,
                cedula = cedula,
                telefono = telefono,
                correo = correo,
                departamento = departamento,
                municipio = municipio,
                tipoCliente = tipoCliente
            )
        }
//...

        composable(Screen.Hventas.route) {
            HventasScreen(
                onBackClick = { navController.popBackStack() },
                onNewVentaClick = { navController.navigate(Screen.Nventa.route) }
            )
        }

        // 🟥 ADMIN
        composable(Screen.ProduccionAdmin.route) {
            ProduccionAdmin(
                onBackClick = { navController.navigate(Screen.HomeAdmin.route) },
                navTo = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.ClientesAdmin.route) {
            ClientesScreenAdmin(onBackClick = { navController.navigate(Screen.HomeAdmin.route) })
        }

        composable(Screen.InventarioAdmin.route) {
            InventarioScreenAdmin(onBackClick = { navController.navigate(Screen.HomeAdmin.route) })
        }

        composable(Screen.EstadisticasAdmin.route) {
            EstadisticasScreenAdmin(onBackClick = { navController.navigate(Screen.HomeAdmin.route) })
        }

        composable(Screen.AgregarTareaAdmin.route) {
            AgregarTareaScreenAdmin(
                onBackClick = { navController.navigate(Screen.ProduccionAdmin.route) },
                navTo = { route -> navController.navigate(route) }
            )
        }
    }
}

