package org.lelestacia.posle.ui.screen.transaction_search

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.TransactionSearchComponent
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.state_event.TransactionSearchEvent
import org.lelestacia.posle.domain.state_event.TransactionSearchEvent.OnSearchQueryChange
import org.lelestacia.posle.domain.state_event.TransactionSearchState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.screen.transaction_history.TransactionItem
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.BurgundyRed
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.cd_back
import posle.shared.generated.resources.label_search
import posle.shared.generated.resources.label_search_for_transaction
import posle.shared.generated.resources.title_transaction_not_found

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSearchScreen(
    component: TransactionSearchComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()
    val searchResults = component.searchResults.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.label_search),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { component.onEvent(TransactionSearchEvent.OnPop) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            BorderedTextField(
                value = state.searchQuery,
                onValueChange = { newSearchQuery ->
                    component.onEvent(OnSearchQueryChange(newSearchQuery))
                },
                label = stringResource(Res.string.label_search_for_transaction),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BurgundyRed
                    )
                },
                modifier = Modifier.padding(12.dp)
            )

            AnimatedContent(
                targetState = searchResults.itemCount > 0,
                modifier = Modifier.weight(1F)
            ) { isNotEmpty ->
                when (isNotEmpty) {
                    true -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                bottom = 12.dp,
                                start = 12.dp,
                                end = 12.dp
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = searchResults.itemCount,
                                key = searchResults.itemKey { it.id }
                            ) { index ->
                                searchResults[index]?.let { transaction ->
                                    Column(modifier = Modifier.animateItem()) {
                                        TransactionItem(
                                            transaction = transaction,
                                            settings = state.settings,
                                            onClick = {
                                                component.onEvent(
                                                    TransactionSearchEvent.OnNavigateTo(
                                                        Config.TransactionView(transaction)
                                                    )
                                                )
                                            }
                                        )
                                        if (index != searchResults.itemCount - 1) {
                                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        }
                                    }
                                }
                            }
                        }
                    }

                    false -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(Icons.Default.SearchOff, Icons.Default.SearchOff.name)
                            Text(
                                stringResource(Res.string.title_transaction_not_found),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviewTransactionSearchScreen() {
    AppTheme {
        TransactionSearchScreen(
            component = object : TransactionSearchComponent {
                override val state = kotlinx.coroutines.flow.MutableStateFlow(TransactionSearchState())
                override val searchResults = kotlinx.coroutines.flow.flowOf(androidx.paging.PagingData.from(emptyList<Transaction>()))
                override fun onEvent(event: TransactionSearchEvent) {}
            }
        )
    }
}
