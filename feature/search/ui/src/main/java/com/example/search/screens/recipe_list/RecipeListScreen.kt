package com.example.search.screens.recipe_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.common.utils.UiText
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.example.common.navigation.NavigationRoute
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeListScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel,
    navHostController: NavHostController,
    onClick: (String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = viewModel.navigation) {
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle).collectLatest {
            when (it) {
                is RecipeList.Navigation.GoToFavoriteScreen -> {
                    navHostController.navigate(NavigationRoute.FavouriteScreen.route)
                }

                is RecipeList.Navigation.GoToRecipeDetails -> {
                    navHostController.navigate(NavigationRoute.RecipeDetails.sendId(it.id))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.onEvent(RecipeList.Event.SearchRecipe(query))
                },
                placeholder = {
                    Text(text = "Search Recipe", style = MaterialTheme.typography.bodySmall)
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(RecipeList.Event.FavoriteScreen)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Go to favourite screen"
                )
            }
        }
    ) { paddingValues ->
        if (uiState.value.isLoading) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (uiState.value.error !is UiText.Idle) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Text(text = "No recipes found")
            }
        }

        uiState.value.data?.let { list ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(list) { recipe ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onClick(recipe.idMeal) },
                    ) {
                        AsyncImage(
                            model = recipe.strMealThumb,
                            contentDescription = recipe.strCategory,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxSize()
                                .height(240.dp)

                        )

                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = recipe.strMeal, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = recipe.strInstructions,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 4
                            )

                            if (recipe.strTags.isNotEmpty()) {
                                FlowRow {
                                    recipe.strTags.split(",").forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .wrapContentSize()
                                                .background(
                                                    color = Color.White,
                                                    shape = RoundedCornerShape(percent = 50)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.Red,
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = tag,
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}