package com.example.search.screens.recipe_details

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.common.navigation.NavigationRoute
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
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.navigation) {
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest { navigation ->
                when (navigation) {
                    is com.example.search.screens.recipe_details.RecipeDetails.Navigation.GoToRecipeListScreen -> {
                        navHostController.popBackStack()
                    }

                    is com.example.search.screens.recipe_details.RecipeDetails.Navigation.GoToMediaPlayer -> {
                        val youtubeUrl = navigation.youtubeUrl.split("v=").last()
                        navHostController.navigate(NavigationRoute.MediaPlayer.sendUrl(youtubeUrl))
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
                        style = MaterialTheme.typography.headlineSmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigationClick.invoke() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "Go back to recipe list",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            uiState.value.data?.let {
                                onFavouriteClick.invoke(it)
                                Toast.makeText(
                                    context,
                                    "Recipe added to favourites",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Mark recipe as favourite",
                        )
                    }
                    IconButton(
                        onClick = {
                            uiState.value.data?.let {
                                onDelete.invoke(it)
                                Toast.makeText(
                                    context,
                                    "Recipe remove from favourites",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Remove recipe from favourite"
                        )
                    }
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
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                                    text = ingredient.first,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
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

                        Spacer(modifier = Modifier.height(32.dp))
                        TextButton(
                            onClick = {
                                viewModel.onEvent(
                                    com.example.search.screens.recipe_details.RecipeDetails.Event.GoToMediaPlayer(
                                        recipe.strYoutube
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = "Watch Youtube Video",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}