package com.manosprojects.themoviedb.features.moviedetails.views

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.manosprojects.themoviedb.R
import com.manosprojects.themoviedb.features.home.views.RatingStarComponent
import com.manosprojects.themoviedb.features.moviedetails.contract.MovieDetailsContract
import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsModel
import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsReviewModel
import com.manosprojects.themoviedb.features.moviedetails.viewmodels.MovieDetailsViewModel

@Composable
fun MovieDetailsScreen(
    movieId: Long,
) {
    val viewModel =
        hiltViewModel<MovieDetailsViewModel, MovieDetailsViewModel.MovieDetailsViewModelFactory> { factory ->
            factory.create(movieId = movieId)
        }

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect {
            val errorMessage = "Something went wrong with the request"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    val state = viewModel.uiState.collectAsState().value

    when (state) {
        is MovieDetailsContract.State.Initial -> return
        is MovieDetailsContract.State.Data -> MovieDetailsScreen(movieDetailsModel = state.movieDetails)
    }

}

@Composable
private fun MovieDetailsScreen(movieDetailsModel: MovieDetailsModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
    ) {
        movieDetailsModel.image?.let {
            Box {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(
                title = movieDetailsModel.title,
                genres = movieDetailsModel.genres,
                isFavourite = movieDetailsModel.isFavourite
            )
            DateAndRating(
                releaseDate = movieDetailsModel.releaseDate,
                rating = movieDetailsModel.rating
            )
            Runtime(
                runtime = movieDetailsModel.runtime
            )
            Description(
                descriptionContent = movieDetailsModel.description
            )
            Reviews(
                reviews = movieDetailsModel.reviews
            )
            SimilarMovies(
                similarMovies = movieDetailsModel.similarMovies
            )
        }

    }
}

@Composable
private fun Header(
    title: String,
    genres: List<String>,
    isFavourite: Boolean
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (column, favourite) = createRefs()
        Column(
            modifier = Modifier.constrainAs(column) {
                start.linkTo(parent.start)
                end.linkTo(favourite.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = genres.joinToString(separator = ", "),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Image(
            modifier = Modifier.constrainAs(favourite) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            painter = painterResource(id = if (isFavourite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_24_unselected),
            contentDescription = null
        )
    }
}

@Composable
private fun DateAndRating(
    releaseDate: String,
    rating: Float,
) {
    Column {
        Text(text = releaseDate, style = MaterialTheme.typography.bodyLarge)
        RatingStarComponent(rating = rating.toDouble())
    }
}

@Composable
private fun Runtime(
    runtime: String
) {
    Paragraph(title = "Runtime") {
        Text(text = runtime, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun Description(
    descriptionContent: String,
) {
    Paragraph(title = "Description") {
        Text(text = descriptionContent, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Reviews(
    reviews: List<MovieDetailsReviewModel>
) {
    Paragraph(title = "Reviews") {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reviews.forEach {
                Column {
                    Text(text = it.author, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = it.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SimilarMovies(
    similarMovies: List<Bitmap>
) {
    Paragraph(title = "Similar Movies") {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            similarMovies.forEach {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .width(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
private fun Paragraph(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}