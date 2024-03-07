package com.manosprojects.themoviedb.features.home.views

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.manosprojects.themoviedb.R

@Composable
fun MovieComponent(
    title: String,
    releaseDate: String,
    rating: Float,
    isFavourite: Boolean,
    imageUrl: String,
    onFavouritePressed: () -> Unit,
    onMoviePressed: () -> Unit,
) {
    var isImageLoading: Boolean by remember {
        mutableStateOf(true)
    }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(coil.size.Size.ORIGINAL)
            .build(),
        onSuccess = { isImageLoading = false })

    if (isImageLoading) {
        ShimmerContent()
    } else {
        val shape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
                .clip(shape)
                .border(color = MaterialTheme.colorScheme.outline, width = 4.dp, shape = shape)
                .clickable {
                    onMoviePressed()
                }
        ) {
            BackgroundImage(
                painter = painter,
            )
            Content(
                title = title,
                rating = rating,
                releaseDate = releaseDate,
                isFavourite = isFavourite,
                onFavouritePressed = onFavouritePressed
            )

        }
    }
}

@Composable
private fun BackgroundImage(painter: AsyncImagePainter) {
    Image(painter = painter, contentDescription = null)
}

@Composable
private fun BoxScope.Content(
    title: String,
    rating: Float,
    releaseDate: String,
    isFavourite: Boolean,
    onFavouritePressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomStart),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingStarComponent(
                rating = rating.toDouble()
            )

            Text(
                text = releaseDate,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                style = MaterialTheme.typography.bodySmall
            )

            IconButton(onClick = onFavouritePressed) {
                Image(
                    painter = painterResource(id = if (isFavourite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_24_unselected),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ShimmerContent() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(164.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .height(24.dp)
                .fillMaxWidth()
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .shimmerEffect()
        )
    }
}

private fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = "",
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}


@Preview
@Composable
private fun MovieComponentPreview() {
    MovieComponent(
        title = "Title",
        releaseDate = "23-12-25",
        rating = 5.0f,
        imageUrl = "image",
        isFavourite = false,
        onFavouritePressed = { },
        onMoviePressed = {})
}
