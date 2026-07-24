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
import org.lelestacia.posle.domain.component.SettingComponentImpl
import org.lelestacia.posle.domain.component.TransactionHistoryComponentImpl
import org.lelestacia.posle.domain.component.TransactionListComponentImpl
import org.lelestacia.posle.domain.component.TransactionProductConfigComponentImpl
import org.lelestacia.posle.domain.component.TransactionViewComponentImpl
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditComponentImpl
import org.lelestacia.posle.domain.component.dashboard.DashboardComponentImpl
import org.lelestacia.posle.domain.component.dashboard.DashboardNavigation
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddEditComponentImpl
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddVariantsViewComponentImpl
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponentImpl
import org.lelestacia.posle.domain.component.product_list.ProductListComponentImpl
import org.lelestacia.posle.domain.component.qr_scanner.QrScannerComponentImpl
import org.lelestacia.posle.domain.component.transaction_add.TransactionAddComponentImpl
import org.lelestacia.posle.domain.component.transaction_add.TransactionAddNavigation
import org.lelestacia.posle.domain.component.transaction_recap.TransactionRecapComponentImpl
import org.lelestacia.posle.domain.component.transaction_recap_product_view.TransactionRecapProductViewComponentImpl
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.BundleRepository
import org.lelestacia.posle.domain.repository.CategoryRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.repository.VariantRepository
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditNavigation
import org.lelestacia.posle.navigation.Child.*
import org.lelestacia.posle.navigation.NavChild.ProductInboundOutbound
import org.lelestacia.posle.navigation.NavChild.ProductList
import org.lelestacia.posle.navigation.NavChild.Setting
import org.lelestacia.posle.navigation.NavChild.TransactionAdd
import org.lelestacia.posle.navigation.NavChild.TransactionHistory
import kotlin.String
import kotlin.Unit
import kotlin.getValue
import org.lelestacia.posle.navigation.Config.BundleAddEdit as BundleAddEditConfig
import org.lelestacia.posle.navigation.Config.Dashboard as DashboardConfig
import org.lelestacia.posle.navigation.Config.ProductAddEdit as ProductAddEditConfig
import org.lelestacia.posle.navigation.Config.QrScanner as QrScannerConfig
import org.lelestacia.posle.navigation.Config.TransactionList as TransactionListConfig
import org.lelestacia.posle.navigation.Config.TransactionProduct as TransactionProductConfig
import org.lelestacia.posle.navigation.Config.TransactionRecapProductView as TransactionRecapProductViewConfig
import org.lelestacia.posle.navigation.Config.TransactionView as TransactionViewConfig
import org.lelestacia.posle.navigation.Config.VariantView as VariantViewConfig
import org.lelestacia.posle.navigation.NavConfig.ProductList as ProductListConfig
import org.lelestacia.posle.navigation.NavConfig.Setting as SettingConfig
import org.lelestacia.posle.navigation.NavConfig.TransactionAdd as TransactionAddConfig
import org.lelestacia.posle.navigation.NavConfig.TransactionHistory as TransactionHistoryConfig
import org.lelestacia.posle.navigation.NavConfig.TransactionRecap as TransactionRecapConfig

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
    private val bundleRepository by inject<BundleRepository>(BundleRepository::class.java)


    // Navigation stacks
    private val rootNavigation = StackNavigation<Config>()
    private val tabNavigation = StackNavigation<NavConfig>()

    //  Helper
    private var onVariantsSelected: ((List<Variant>) -> Unit)? = null
    private var onProductConfigConfirmed: ((TransactionItemState, List<Variant>) -> Unit)? = null
    private var onQrScanned: ((String) -> Unit)? = null

    val tabChildren: Value<ChildStack<NavConfig, NavChild>> = childStack(
        source = tabNavigation,
        serializer = NavConfig.serializer(),
        initialStack = { listOf(TransactionAddConfig) },
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
            is TransactionHistoryConfig -> TransactionHistory(
                TransactionHistoryComponentImpl(
                    componentContext = context,
                    settingManager = settingManager,
                    repository = transactionRepository,
                    onNavigation = rootNavigation::pushNew
                )
            )

            TransactionRecapConfig -> NavChild.TransactionRecap(
                TransactionRecapComponentImpl(
                    componentContext = context,
                    settingManager = settingManager,
                    transactionRepository = transactionRepository,
                    onNavigateToTransactionView = {
                        rootNavigation.pushNew(TransactionViewConfig(it))
                    },
                    onNavigateToRecapProductView = {
                        rootNavigation.pushNew(TransactionRecapProductViewConfig(it))
                    }
                )
            )

            SettingConfig -> Setting(
                SettingComponentImpl(
                    componentContext = context,
                    settingManager = settingManager
                )
            )

            ProductListConfig -> ProductList(
                ProductListComponentImpl(
                    componentContext = context,
                    settingManager = settingManager,
                    productRepository = productRepository,
                    categoryRepository = categoryRepository,
                    bundleRepository = bundleRepository,
                    onNavigate = rootNavigation::pushToFront
                )
            )

            TransactionAddConfig -> TransactionAdd(
                TransactionAddComponentImpl(
                    snackBarHostState = snackbarHostState,
                    componentContext = context,
                    productRepository = productRepository,
                    bundleRepository = bundleRepository,
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

                        override fun onNavigateToQRScanner(onResult: (String) -> Unit) {
                            onQrScanned = onResult
                            rootNavigation.pushNew(configuration = QrScannerConfig)
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
            DashboardConfig -> Dashboard(
                DashboardComponentImpl(
                    componentContext = context,
                    navChildren = tabChildren,
                    settingManager = settingManager,
                    onNavigation = ::handleDashboardNavigation
                )
            )

            is TransactionProductConfig -> TransactionProductConfig(
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

            TransactionListConfig -> TransactionList(
                TransactionListComponentImpl(
                    componentContext = context,
                    onNavigateTo = rootNavigation::pushToFront
                )
            )

            is TransactionViewConfig -> TransactionView(
                TransactionViewComponentImpl(
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

            is ProductAddEditConfig -> ProductAddEdit(
                ProductAddEditComponentImpl(
                    componentContext = context,
                    mode = config.addEdit,
                    product = config.product,
                    snackbarHostState = snackbarHostState,
                    productRepository = productRepository,
                    variantRepository = variantRepository,
                    stockRepository = stockRepository,
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

                        override fun onNavigateToQRScanner(
                            onResult: (String) -> Unit
                        ) {
                            onQrScanned = onResult
                            rootNavigation.pushNew(configuration = QrScannerConfig)
                        }
                    },
                )
            )

            QrScannerConfig -> QrScanner(
                component = QrScannerComponentImpl(
                    componentContext = context,
                    onQrScanned = {
                        onQrScanned?.invoke(it)
                        onQrScanned = null
                        rootNavigation.pop()
                    }
                )
            )

            is VariantViewConfig -> VariantView(
                ProductAddVariantsViewComponentImpl(
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

            is BundleAddEditConfig -> BundleAddEdit(
                component = BundleAddEditComponentImpl(
                    componentContext = context,
                    mode = config.addEdit,
                    bundle = config.bundle,
                    snackbarHostState = snackbarHostState,
                    productRepository = productRepository,
                    bundleRepository = bundleRepository,
                    onDone = rootNavigation::pop
                )
            )

            is TransactionRecapProductViewConfig -> TransactionRecapProductView(
                component = TransactionRecapProductViewComponentImpl(
                    componentContext = context,
                    products = config.items,
                    onPop = rootNavigation::pop
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
