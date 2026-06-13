package org.lelestacia.posle.navigation

import androidx.compose.material3.SnackbarHostState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import org.koin.java.KoinJavaComponent.inject
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.component.DashboardComponent
import org.lelestacia.posle.domain.component.DashboardNavigation
import org.lelestacia.posle.domain.component.ProductAddEditComponent
import org.lelestacia.posle.domain.component.ProductListComponent
import org.lelestacia.posle.domain.component.SettingComponent
import org.lelestacia.posle.domain.component.TransactionAddComponent
import org.lelestacia.posle.domain.component.TransactionHistoryComponent
import org.lelestacia.posle.domain.component.TransactionListComponent
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.navigation.Child.AddProduct
import org.lelestacia.posle.navigation.NavChild.ProductList
import org.lelestacia.posle.navigation.NavChild.Setting
import org.lelestacia.posle.navigation.NavChild.Transaction

class PosLeComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    val snackbarHostState = SnackbarHostState()

    // Dependencies
    private val settingManager by inject<SettingManager>(SettingManager::class.java)
    private val productRepository by inject<ProductRepository>(ProductRepository::class.java)
    private val transactionRepository by inject<TransactionRepository>(TransactionRepository::class.java)


    // Navigation stacks
    private val rootNavigation = StackNavigation<Config>()
    private val tabNavigation = StackNavigation<NavConfig>()

    val tabChildren: Value<ChildStack<NavConfig, NavChild>> = childStack(
        source = tabNavigation,
        serializer = NavConfig.serializer(),
        initialStack = { listOf(NavConfig.Transaction) },
        key = "tabNavigationChildStack",
        handleBackButton = false,
        childFactory = ::createTabChild
    )

    val children: Value<ChildStack<Config, Child>> = childStack(
        source = rootNavigation,
        serializer = Config.serializer(),
        initialStack = { listOf(Config.Dashboard) },
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createTabChild(config: NavConfig, context: ComponentContext): NavChild =
        when (config) {
            is NavConfig.Transaction -> Transaction(
                TransactionHistoryComponent(
                    componentContext = context,
                    repository = transactionRepository,
                    onNavigation = { rootNavigation.pushNew(it) }
                )
            )
            NavConfig.Setting -> Setting(
                SettingComponent(
                    componentContext = context,
                    settingManager = settingManager
                )
            )

            NavConfig.ProductList -> ProductList(
                ProductListComponent(
                    componentContext = context,
                    repository = productRepository
                )
            )
        }

    private fun createChild(config: Config, context: ComponentContext): Child =
        when (config) {
            Config.Dashboard -> Child.Dashboard(
                DashboardComponent(
                    componentContext = context,
                    children = tabChildren,
                    onNavigation = ::handleDashboardNavigation
                )
            )

            Config.TransactionAdd -> Child.TransactionAdd(
                TransactionAddComponent(
                    componentContext = context,
                    productRepository = productRepository,
                    transactionRepository = transactionRepository,
                    settingManager = settingManager,
                    onNavigateTo = { rootNavigation.replaceCurrent(it) }
                )
            )

            Config.TransactionList -> Child.TransactionList(
                TransactionListComponent(
                    componentContext = context,
                    onNavigateTo = { rootNavigation.pushToFront(it) }
                )
            )

            is Config.TransactionView -> Child.TransactionView(
                TransactionViewComponent(
                    componentContext = context,
                    transaction = config.transaction,
                    settingManager = settingManager,
                    onNavigation = { navigation ->
                        when (navigation) {
                            TransactionViewNavigation.OnPop -> rootNavigation.pop()
                        }
                    }
                )
            )

            is Config.AddEditProduct -> AddProduct(
                ProductAddEditComponent(
                    componentContext = context,
                    mode = config.addEdit,
                    product = config.product,
                    snackbarHostState = snackbarHostState,
                    repository = productRepository,
                    onPop = { rootNavigation.pop() }
                )
            )
        }

    private fun handleDashboardNavigation(navigation: DashboardNavigation) {
        when (navigation) {
            is DashboardNavigation.BottomNav -> tabNavigation.pushToFront(navigation.navConfig)
            is DashboardNavigation.Nav -> rootNavigation.pushNew(navigation.config)
        }
    }
}