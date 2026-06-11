package org.lelestacia.posle.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import org.koin.java.KoinJavaComponent.inject
import org.lelestacia.posle.domain.component.AddTransactionComponent
import org.lelestacia.posle.domain.component.DashboardComponent
import org.lelestacia.posle.domain.component.ProductAddEditComponent
import org.lelestacia.posle.domain.component.ProductListComponent
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.navigation.Child.AddEditTransaction
import org.lelestacia.posle.navigation.Child.AddProduct
import org.lelestacia.posle.navigation.Child.Dashboard

class PosLeComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val productRepository by inject<ProductRepository>(ProductRepository::class.java)

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
                Config.AddTransaction -> AddEditTransaction(AddTransactionComponent(context))

                Config.Dashboard -> Dashboard(
                    DashboardComponent(
                        componentContext = context,
                        children = navChildren,
                        onNavigateTo = { config ->
                            bottomNavigation.pushToFront(config)
                        },
                        onNavigateToAddEditProduct = { addEdit, product ->
                            parentNavigation.pushNew(Config.AddEditProduct(addEdit, product))
                        }
                    )
                )

                is Config.AddEditProduct -> AddProduct(
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