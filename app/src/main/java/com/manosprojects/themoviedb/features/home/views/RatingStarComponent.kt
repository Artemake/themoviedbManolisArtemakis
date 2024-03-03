package com.manosprojects.themoviedb.features.home.views

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.manosprojects.themoviedb.R

@Composable
fun RatingStarComponent(
    modifier: Modifier = Modifier,
    rating: Double,
    maxRating: Double = 10.0,
    stars: Int = 5,
) {

    Row(modifier = modifier) {
        for (i in 1..stars) {
            Icon(
                painter = painterResource(
                    id = getStarResource(
                        starIndex = i,
                        maxRatingToStarsRatio = maxRating / stars,
                        rating = rating
                    )
                ),
                tint = Color.Yellow,
                contentDescription = null
            )
        }
    }
}

private fun getStarResource(starIndex: Int, maxRatingToStarsRatio: Double, rating: Double): Int {

    return when {
        starIndex * maxRatingToStarsRatio < rating -> R.drawable.baseline_star_24
        starIndex * maxRatingToStarsRatio > rating && (starIndex - 1) * maxRatingToStarsRatio < rating -> R.drawable.baseline_star_half_24
        else -> R.drawable.baseline_star_outline_24
    }
}

@Preview
@Composable
fun RatingPreview() {
    RatingStarComponent(rating = 9.0)
}
