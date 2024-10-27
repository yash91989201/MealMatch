package com.example.search.screens.recipe_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.common.utils.UiText
import kotlinx.coroutines.flow.collectLatest
import com.example.search.domain.model.RecipeDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailsViewModel,
    navHostController: NavHostController,
    onNavigationClick: () -> Unit,
    onDelete: (RecipeDetails) -> Unit,
    onFavouriteClick: (RecipeDetails) -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = viewModel.navigation) {
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest { navigation ->
                when (navigation) {
                    com.example.search.screens.recipe_details.RecipeDetails.Navigation.GoToRecipeListScreen -> {
                        navHostController.popBackStack()
                    }
                }
            }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.value.data?.strMeal.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = "Go back to recipe list",
                        modifier = Modifier.clickable { onNavigationClick.invoke() })
                },
                actions = {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Mark recipe as favourite",
                        modifier = Modifier.clickable {
                            uiState.value.data?.let {
                                onFavouriteClick.invoke(it)
                            }
                        }
                    )
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Remove recipe from favourite"
                    )
                }
            )
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
                Text(text = "Error occurred")
            }
        }

        uiState.value.data?.let { recipe ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = recipe.strMealThumb,
                    contentDescription = recipe.strMeal,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                )
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = recipe.strInstructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ingredients List",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    recipe.ingredientsPair.forEach { ingredient ->
                        if (ingredient.first.isNotEmpty() || ingredient.second.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                AsyncImage(
                                    model = "https://www.themealdb.com/images/ingredients/${ingredient.first}.png",
                                    contentDescription = ingredient.first,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(color = Color.White, shape = CircleShape)
                                        .clip(CircleShape)
                                )
                                Text(
                                    text = ingredient.second,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (recipe.strYoutube.isNotEmpty()) {
                        Text(
                            text = "Watch Youtube Video",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}