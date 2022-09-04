package com.wafflestudio.snutt2

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.*
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.map

@Navigator.Name("slide_composable")
class ComposeSlideNavigator : Navigator<ComposeSlideNavigator.Destination>() {

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.forEach { entry ->
            state.pushWithTransition(entry)
        }
    }

    override fun createDestination(): Destination {
        return Destination(this) { }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
    }

    fun onTransitionComplete(entry: NavBackStackEntry) {
        state.markTransitionComplete(entry)
    }

    @NavDestination.ClassType(Composable::class)
    class Destination(
        navigator: ComposeSlideNavigator,
        val content: @Composable (NavBackStackEntry) -> Unit
    ) : NavDestination(navigator)

    companion object {
        const val NAME = "slide_composable"
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHostWithSlideAnimation(
    navController: NavHostController,
    graph: NavGraph,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "NavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedDispatcher = onBackPressedDispatcherOwner?.onBackPressedDispatcher

    // Setup the navController with proper owners
    navController.setLifecycleOwner(lifecycleOwner)
    navController.setViewModelStore(viewModelStoreOwner.viewModelStore)
    if (onBackPressedDispatcher != null) {
        navController.setOnBackPressedDispatcher(onBackPressedDispatcher)
    }
    // Ensure that the NavController only receives back events while
    // the NavHost is in composition
    DisposableEffect(navController) {
        navController.enableOnBackPressed(true)
        onDispose {
            navController.enableOnBackPressed(false)
        }
    }

    // Then set the graph
    navController.graph = graph

    val saveableStateHolder = rememberSaveableStateHolder()

    // Find the ComposeNavigator, returning early if it isn't found
    // (such as is the case when using TestNavHostController)
    val composeNavigator = navController.navigatorProvider.get<Navigator<out NavDestination>>(
        ComposeSlideNavigator.NAME
    ) as? ComposeSlideNavigator ?: return
    val visibleEntries by remember(navController.visibleEntries) {
        navController.visibleEntries.map {
            it.filter { entry ->
                entry.destination.navigatorName == ComposeSlideNavigator.NAME
            }
        }
    }.collectAsState(emptyList())


    var prevBackStackSize by remember { mutableStateOf(navController.backQueue.size) }
    var isForward by remember { mutableStateOf(true) }
    LaunchedEffect(navController.currentBackStackEntryAsState().value) {
        val backStackSize = navController.backQueue.size
        if (prevBackStackSize != backStackSize) {
            isForward = prevBackStackSize > backStackSize
            prevBackStackSize = backStackSize
        }
    }

    val backStackEntry = visibleEntries.lastOrNull()

    var initialEnterAnimation by remember { mutableStateOf(true) }
    if (backStackEntry != null) {
        // while in the scope of the composable, we provide the navBackStackEntry as the
        // ViewModelStoreOwner and LifecycleOwner
        AnimatedContent(targetState = backStackEntry.id,
            modifier = modifier,
            transitionSpec = {
                if (isForward)
                    slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) with fadeOut(
                        targetAlpha = 0.0f
                    )
                else
                    fadeIn(initialAlpha = 0.0f) with slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
            }
        ) { targetState ->
            val lastEntry = visibleEntries.last { entry ->
                targetState == entry.id
            }
            // We are disposing on a Unit as we only want to dispose when the CrossFade completes
            DisposableEffect(Unit) {
                if (initialEnterAnimation) {
                    // There's no animation for the initial entering,
                    // so we can instantly mark the transition as complete
                    visibleEntries.forEach { entry ->
                        composeNavigator.onTransitionComplete(entry)
                    }
                    initialEnterAnimation = false
                }
                onDispose {
                    visibleEntries.forEach { entry ->
                        composeNavigator.onTransitionComplete(entry)
                    }
                }
            }

            lastEntry.LocalOwnersProvider(saveableStateHolder) {
                (lastEntry.destination as ComposeSlideNavigator.Destination).content(lastEntry)
            }
        }
    }
}

@Composable
fun NavHostWithSlideAnimation(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHostWithSlideAnimation(
        navController,
        remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier
    )
}

fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        ComposeSlideNavigator.Destination(provider[ComposeSlideNavigator::class], content).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}
