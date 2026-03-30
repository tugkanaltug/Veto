package veto.call.blocker.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object BlockList : Screen("block_list")
    object CallLog : Screen("call_log")
}
