package veto.call.blocker.ui

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import veto.call.blocker.ui.blocklist.BlockListScreen
import veto.call.blocker.ui.calllog.CallLogScreen
import veto.call.blocker.ui.navigation.Screen
import veto.call.blocker.ui.onboarding.OnboardingScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VetoApp(viewModel: VetoViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    val contactPermissionState = rememberPermissionState(android.Manifest.permission.READ_CONTACTS)
    val isCallScreeningRoleGranted = isCallScreeningRoleHeld(context)
    
    val startDestination = if (contactPermissionState.status.isGranted && isCallScreeningRoleGranted) {
        Screen.BlockList.route
    } else {
        Screen.Onboarding.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != Screen.Onboarding.route) {
                NavigationBar {
                    val items = listOf(
                        Triple(Screen.BlockList.route, "Blocked", Icons.AutoMirrored.Filled.List),
                        Triple(Screen.CallLog.route, "Logs", Icons.Default.History)
                    )
                    items.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onPermissionsGranted = {
                    navController.navigate(Screen.BlockList.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.BlockList.route) {
                BlockListScreen(viewModel = viewModel)
            }
            composable(Screen.CallLog.route) {
                CallLogScreen(viewModel = viewModel)
            }
        }
    }
}

private fun isCallScreeningRoleHeld(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
        roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    } else {
        true
    }
}
