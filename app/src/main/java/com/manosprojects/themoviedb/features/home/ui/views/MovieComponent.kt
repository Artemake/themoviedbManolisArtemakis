package com.manosprojects.themoviedb.features.home.ui.views

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.manosprojects.themoviedb.R

@Composable
fun MovieComponent(
    title: String,
    releaseDate: String,
    rating: String,
    isFavourite: Boolean,
    image: Bitmap?,
    onFavouritePressed: () -> Unit,
    onMoviePressed: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable {
                onMoviePressed()
            }
    ) {
        Column {
            image?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = null)
            }
            IconButton(onClick = onFavouritePressed) {
                Image(
                    painter = painterResource(id = if (isFavourite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24),
                    contentDescription = null
                )
            }
            Text(text = title)
            Text(text = releaseDate)
            Text(text = rating)
        }
    }
}

@Preview
@Composable
private fun MovieComponentPreview() {
    MovieComponent(
        title = "Title",
        releaseDate = "23-12-25",
        rating = "5.0",
        image = null,
        isFavourite = false,
        onFavouritePressed = { },
        onMoviePressed = {})
}
