package org.lelestacia.posle.navigation

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
import org.lelestacia.posle.domain.component.DashboardComponent
import org.lelestacia.posle.domain.component.DashboardNavigation
import org.lelestacia.posle.domain.component.ProductAddEditComponent
import org.lelestacia.posle.domain.component.ProductListComponent
import org.lelestacia.posle.domain.component.TransactionAddComponent
import org.lelestacia.posle.domain.component.TransactionListComponent
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.navigation.Child.AddProduct
import org.lelestacia.posle.navigation.Child.Dashboard
import org.lelestacia.posle.navigation.Child.TransactionAdd
import org.lelestacia.posle.navigation.Config.AddEditProduct
import org.lelestacia.posle.navigation.Config.TransactionList
import org.lelestacia.posle.navigation.Config.TransactionView

class PosLeComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val productRepository by inject<ProductRepository>(ProductRepository::class.java)
    private val transactionRepository by inject<TransactionRepository>(TransactionRepository::class.java)

    val parentNavigation = StackNavigation<Config>()
    val bottomNavigation = StackNavigation<NavConfig>()


    val navChildren: Value<ChildStack<NavConfig, NavChild>> = childStack(
        source = bottomNavigation,
        serializer = NavConfig.serializer(),
        initialStack = {
            listOf(NavConfig.Transaction)
        },
        key = "bottomNavigationChildStack",
        handleBackButton = false,
        childFactory = { config, context ->
            when (config) {
                NavConfig.Transaction -> NavChild.Transaction
                NavConfig.ProductList -> NavChild.ProductList(
                    ProductListComponent(
                        componentContext = context,
                        repository = productRepository
                    )
                )

                NavConfig.Setting -> NavChild.Setting
            }
        }
    )

    val children = childStack(
        source = parentNavigation,
        serializer = Config.serializer(),
        initialStack = {
            listOf(Config.Dashboard)
        },
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.TransactionAdd -> TransactionAdd(
                    TransactionAddComponent(
                        componentContext = context,
                        productRepository = productRepository,
                        transactionRepository = transactionRepository,
                        onNavigateTo = { config ->
                            parentNavigation.replaceCurrent(config)
                        }
                    )
                )

                Config.Dashboard -> Dashboard(
                    DashboardComponent(
                        componentContext = context,
                        children = navChildren,
                        onNavigation = {
                            when(it) {
                                is DashboardNavigation.BottomNav -> bottomNavigation.pushToFront(it.navConfig)
                                is DashboardNavigation.Nav -> parentNavigation.pushNew(it.config)
                            }
                        }
                    )
                )

                TransactionList -> Child.TransactionList(
                    component = TransactionListComponent(
                        componentContext = context,
                        onNavigateTo = { config ->
                            parentNavigation.pushToFront(config)
                        }
                    )
                )

                is TransactionView -> Child.TransactionView(
                    component = TransactionViewComponent(
                        componentContext = context,
                        transaction = config.transaction,
                        onNavigation = { navigation ->
                            when(navigation) {
                                TransactionViewNavigation.OnPop -> parentNavigation.pop()
                            }
                        }
                    )
                )

                is AddEditProduct -> AddProduct(
                    ProductAddEditComponent(
                        componentContext = context,
                        mode = config.addEdit,
                        onPop = {
                            parentNavigation.pop()
                        },
                        product = config.product,
                        repository = productRepository
                    )
                )

            }
        }
    )
}