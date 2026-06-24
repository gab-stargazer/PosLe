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
import org.lelestacia.posle.domain.component.DashboardComponentImpl
import org.lelestacia.posle.domain.component.DashboardNavigation
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentImpl
import org.lelestacia.posle.domain.component.ProductListComponentImpl
import org.lelestacia.posle.domain.component.SettingComponent
import org.lelestacia.posle.domain.component.TransactionAddComponent
import org.lelestacia.posle.domain.component.TransactionAddNavigation
import org.lelestacia.posle.domain.component.TransactionHistoryComponent
import org.lelestacia.posle.domain.component.TransactionListComponent
import org.lelestacia.posle.domain.component.TransactionProductConfigComponentImpl
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddEditComponent
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddVariantsViewComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.CategoryRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.repository.VariantRepository
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditNavigation
import org.lelestacia.posle.navigation.NavChild.ProductInboundOutbound
import org.lelestacia.posle.navigation.NavChild.ProductList
import org.lelestacia.posle.navigation.NavChild.Setting
import org.lelestacia.posle.navigation.NavChild.TransactionAdd
import org.lelestacia.posle.navigation.NavChild.TransactionHistory
import org.lelestacia.posle.navigation.Config.Dashboard as DashboardConfig
import org.lelestacia.posle.navigation.Config.ProductAddEdit as ProductAddEditConfig
import org.lelestacia.posle.navigation.Config.TransactionList as TransactionListConfig
import org.lelestacia.posle.navigation.Config.TransactionProduct as TransactionProductConfig
import org.lelestacia.posle.navigation.Config.TransactionView as TransactionViewConfig
import org.lelestacia.posle.navigation.Config.VariantView as VariantViewConfig

class PosLeComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    val snackbarHostState = SnackbarHostState()

    // Dependencies
    private val settingManager by inject<SettingManager>(SettingManager::class.java)
    private val productRepository by inject<ProductRepository>(ProductRepository::class.java)
    private val categoryRepository by inject<CategoryRepository>(CategoryRepository::class.java)
    private val variantRepository by inject<VariantRepository>(VariantRepository::class.java)
    private val stockRepository by inject<StockRepository>(StockRepository::class.java)
    private val transactionRepository by inject<TransactionRepository>(TransactionRepository::class.java)


    // Navigation stacks
    private val rootNavigation = StackNavigation<Config>()
    private val tabNavigation = StackNavigation<NavConfig>()

    //  Helper
    private var onVariantsSelected: ((List<Variant>) -> Unit)? = null
    private var onProductConfigConfirmed: ((TransactionItemState, List<Variant>) -> Unit)? = null

    val tabChildren: Value<ChildStack<NavConfig, NavChild>> = childStack(
        source = tabNavigation,
        serializer = NavConfig.serializer(),
        initialStack = { listOf(NavConfig.TransactionAdd) },
        key = "tabNavigationChildStack",
        handleBackButton = false,
        childFactory = ::createTabChild
    )

    val children: Value<ChildStack<Config, Child>> = childStack(
        source = rootNavigation,
        serializer = Config.serializer(),
        initialStack = { listOf(DashboardConfig) },
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createTabChild(config: NavConfig, context: ComponentContext): NavChild =
        when (config) {
            is NavConfig.TransactionHistory -> TransactionHistory(
                TransactionHistoryComponent(
                    componentContext = context,
                    settingManager = settingManager,
                    repository = transactionRepository,
                    onNavigation = rootNavigation::pushNew
                )
            )

            NavConfig.Setting -> Setting(
                SettingComponent(
                    componentContext = context,
                    settingManager = settingManager
                )
            )

            NavConfig.ProductList -> ProductList(
                ProductListComponentImpl(
                    componentContext = context,
                    settingManager = settingManager,
                    productRepository = productRepository,
                    categoryRepository = categoryRepository,
                    onNavigate = rootNavigation::pushToFront
                )
            )

            NavConfig.TransactionAdd -> TransactionAdd(
                TransactionAddComponent(
                    componentContext = context,
                    productRepository = productRepository,
                    transactionRepository = transactionRepository,
                    settingManager = settingManager,
                    navigation = object : TransactionAddNavigation {
                        override fun onNavigateTo(config: Config, onComplete: () -> Unit) {
                            rootNavigation.pushToFront(config, onComplete = onComplete)
                        }

                        override fun onNavigateToProductConfig(
                            product: Product,
                            onConfirmed: (TransactionItemState, List<Variant>) -> Unit
                        ) {
                            rootNavigation.pushNew(TransactionProductConfig(product))
                            this@PosLeComponent.onProductConfigConfirmed = onConfirmed
                        }
                    }
                )
            )

            NavConfig.ProductInboundOutbound -> ProductInboundOutbound(
                component = ProductInboundOutboundComponentImpl(
                    componentContext = context,
                    settingManager = settingManager,
                    stockRepository = stockRepository,
                    productRepository = productRepository
                )
            )
        }

    private fun createChild(config: Config, context: ComponentContext): Child =
        when (config) {
            DashboardConfig -> Child.Dashboard(
                DashboardComponentImpl(
                    componentContext = context,
                    navChildren = tabChildren,
                    settingManager = settingManager,
                    onNavigation = ::handleDashboardNavigation
                )
            )

            is TransactionProductConfig -> Child.TransactionProductConfig(
                TransactionProductConfigComponentImpl(
                    componentContext = context,
                    product = config.product,
                    snackbarHostState = snackbarHostState,
                    settingManager = settingManager,
                    onConfirmed = { itemState, variants ->
                        onProductConfigConfirmed?.invoke(itemState, variants)
                        onProductConfigConfirmed = null
                        rootNavigation.pop()
                    }
                )
            )

            TransactionListConfig -> Child.TransactionList(
                TransactionListComponent(
                    componentContext = context,
                    onNavigateTo = rootNavigation::pushToFront
                )
            )

            is TransactionViewConfig -> Child.TransactionView(
                TransactionViewComponent(
                    componentContext = context,
                    transaction = config.transaction,
                    settingManager = settingManager,
                    repository = transactionRepository,
                    onNavigation = { navigation ->
                        when (navigation) {
                            TransactionViewNavigation.OnPop -> rootNavigation.pop()
                        }
                    }
                )
            )

            is ProductAddEditConfig -> Child.ProductAdd(
                ProductAddEditComponent(
                    componentContext = context,
                    mode = config.addEdit,
                    product = config.product,
                    snackbarHostState = snackbarHostState,
                    productRepository = productRepository,
                    variantRepository = variantRepository,
                    navigation = object : ProductAddEditNavigation {
                        override fun onPop() {
                            rootNavigation.pop()
                        }

                        override fun onNavigateToVariantSelection(
                            config: VariantViewConfig,
                            onResult: (List<Variant>) -> Unit
                        ) {
                            onVariantsSelected = onResult
                            rootNavigation.pushNew(config)
                        }
                    },
                )
            )

            is VariantViewConfig -> Child.VariantView(
                ProductAddVariantsViewComponent(
                    componentContext = context,
                    initialSelectedVariants = config.selectedVariants,
                    onVariantsConfirmed = {
                        onVariantsSelected?.invoke(it)
                        onVariantsSelected = null
                        rootNavigation.pop()
                    },
                    onPop = {
                        onVariantsSelected = null
                        rootNavigation.pop()
                    },
                    repository = variantRepository
                )
            )
        }

    private fun handleDashboardNavigation(navigation: DashboardNavigation) {
        when (navigation) {
            is DashboardNavigation.DrawerNav -> {
                tabNavigation.replaceCurrent(
                    navigation.navConfig,
                    onComplete = navigation.callbacks
                )
            }

            is DashboardNavigation.Nav -> rootNavigation.pushNew(navigation.config)
        }
    }
}
