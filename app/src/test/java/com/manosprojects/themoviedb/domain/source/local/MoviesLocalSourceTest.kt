package com.manosprojects.themoviedb.domain.source.local

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.core.content.edit
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.domain.source.local.mappers.mapToCache
import com.manosprojects.themoviedb.domain.source.local.mappers.mapToDomain
import com.manosprojects.themoviedb.sharedpref.SharedPrefKeys
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import com.manosprojects.themoviedb.utils.loadData
import com.manosprojects.themoviedb.utils.storeDataToFile
import com.manosprojects.themoviedb.utils.storeImage
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KFunction

class MoviesLocalSourceTest {

    private val sharedPreferencesMock: SharedPreferences = mockk()
    private val contextMock: Context = mockk()

    private val dataPostfix = "-data"

    private lateinit var moviesLocalSourceImpL: MoviesLocalSourceImpL

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val functionLoadData: (String, Context, Class<LMovie>) -> LMovie? = ::loadData
        val kFunctionLoadData = functionLoadData as KFunction<*>
        val functionStoreDataToFile: (String, List<DMovie>, Context) -> Unit = ::storeDataToFile
        val kFunctionStoreDataToFile = functionStoreDataToFile as KFunction<*>
        mockkStatic(kFunctionLoadData)
        mockkStatic(kFunctionStoreDataToFile)
        mockkStatic(::storeImage)

        moviesLocalSourceImpL = MoviesLocalSourceImpL(
            sharedPreferences = sharedPreferencesMock,
            context = contextMock
        )
    }

    @Test
    fun `when areMoviesStored() is invoked, then assert results and verify interactions`() {
        // given
        every { sharedPreferencesMock.getString(SharedPrefKeys.MOVIE_IDS, null) } returns null

        // when
        val actual = moviesLocalSourceImpL.areMoviesStored()

        // then
        assertThat(actual, equalTo(false))
        verify { sharedPreferencesMock.getString(SharedPrefKeys.MOVIE_IDS, null) }
    }

    @Test
    fun `given movieIds are stored to SharedPreferences, when getMovies() is invoked, then assert results and verify interactions`() {
        // given
        val movieId1 = "1234"
        val movieId2 = "123456"
        every {
            sharedPreferencesMock.getString(
                SharedPrefKeys.MOVIE_IDS,
                null
            )
        } returns "[$movieId1, $movieId2]"
        every {
            loadData(
                dataFile = movieId1 + dataPostfix,
                context = contextMock,
                classOf = LMovie::class.java
            )
        } returns lMovie(movieId1)
        every {
            loadData<LMovie>(
                dataFile = movieId2 + dataPostfix,
                context = contextMock,
                classOf = LMovie::class.java
            )
        } returns lMovie(movieId2)

        // when
        val actual = moviesLocalSourceImpL.getMovies()
        val expected = listOf(
            lMovie(movieId1).mapToDomain(),
            lMovie(movieId2).mapToDomain()
        )

        // then
        assertThat(actual, equalTo(expected))
        verifyAll {
            sharedPreferencesMock.getString(SharedPrefKeys.MOVIE_IDS, null)
            loadData<LMovie>(
                dataFile = movieId1 + dataPostfix,
                context = contextMock,
                classOf = LMovie::class.java
            )
            loadData<LMovie>(
                dataFile = movieId2 + dataPostfix,
                context = contextMock,
                classOf = LMovie::class.java
            )
        }
    }


    @Test
    fun `when isMovieFavourite() is invoked, then assert results and verify interactions`() {
        // given
        val movieId = 1234L
        val key = SharedPrefKeys.FAVORITE_PREFIX + movieId
        every { sharedPreferencesMock.getBoolean(key, false) } returns true

        // when
        val actual = moviesLocalSourceImpL.isMovieFavourite(movieId)

        // then
        assertThat(actual, equalTo(true))
        verify { sharedPreferencesMock.getBoolean(key, false) }
    }

    @Test
    fun `when setFavourite(movieId, isFavourite) is invoked, then assert results and verify interactions`() {
        // given
        val movieId = 1234L
        val key = SharedPrefKeys.FAVORITE_PREFIX + movieId
        every {
            sharedPreferencesMock.edit {
                putBoolean(key, true)
            }
        } just runs

        // when
        moviesLocalSourceImpL.setFavourite(movieId, true)

        // then
        verify {
            sharedPreferencesMock.edit {
                putBoolean(key, true)
            }
        }
    }

    @Test
    fun `when storeMovies(movies) is invoked, then assert results and verify interactions`() {
        // given
        val dataPostfix = "-data"
        val imagePostfix = "-image.png"
        val mockDMovie = DMovie(
            movieId = 1234,
            title = "movie",
            releaseDate = formatStringDateToLocalDate("2024-12-12"),
            rating = 5.0f,
            imageUrl = "movieurlimage.jpeg",
            isFavourite = false,
        )
        val image: Bitmap = mockk()
        val imageFile = mockDMovie.movieId.toString() + imagePostfix
        val dataFile = mockDMovie.movieId.toString() + dataPostfix

        every { storeImage(imageFile = imageFile, image = image, context = contextMock) } just runs
        every { contextMock.filesDir.absolutePath } returns "path"
        every {
            storeDataToFile(
                dataFile = dataFile,
                data = mockDMovie.mapToCache("path/$imageFile"),
                context = contextMock
            )
        } just runs
        every {
            sharedPreferencesMock.edit {
                putString(SharedPrefKeys.MOVIE_IDS, "[\"${mockDMovie.movieId}\"]")
            }
        } just runs

        // when
        moviesLocalSourceImpL.storeMovies(listOf(mockDMovie to image))

        // then
        verifyAll {
            storeImage(imageFile = imageFile, image = image, context = contextMock)
            storeDataToFile(
                dataFile = dataFile,
                data = mockDMovie.mapToCache("path/$imageFile"),
                context = contextMock
            )
            sharedPreferencesMock.edit {
                putString(SharedPrefKeys.MOVIE_IDS, "[\"${mockDMovie.movieId}\"]")
            }
        }
    }

    private fun lMovie(movieId: String) = LMovie(
        movieId = movieId.toLong(),
        title = "title$movieId",
        releaseDate = "2026-12-12",
        rating = 5.0f,
        isFavourite = false,
        imageFile = "imagefile$movieId",
    )

}