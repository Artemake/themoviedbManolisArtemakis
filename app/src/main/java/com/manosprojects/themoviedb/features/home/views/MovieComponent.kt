package com.manosprojects.themoviedb.features.home.views

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.manosprojects.themoviedb.R

@Composable
fun MovieComponent(
    title: String,
    releaseDate: String,
    rating: Float,
    isFavourite: Boolean,
    image: Bitmap?,
    onFavouritePressed: () -> Unit,
    onMoviePressed: () -> Unit,
) {
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
        image?.let {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
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
}

@Preview
@Composable
private fun MovieComponentPreview() {
    MovieComponent(
        title = "Title",
        releaseDate = "23-12-25",
        rating = 5.0f,
        image = null,
        isFavourite = false,
        onFavouritePressed = { },
        onMoviePressed = {})
}
