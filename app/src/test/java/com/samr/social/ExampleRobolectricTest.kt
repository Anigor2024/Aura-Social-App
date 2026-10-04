package com.samr.social

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.samr.social.core.model.MoodType
import com.samr.social.core.model.PostLifetime
import com.samr.social.core.model.SocialLayer
import com.samr.social.core.repository.SamrRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies SAMR brand name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SAMR", appName)
    }

    @Test
    fun `samr repository optimistic like toggle updates count correctly`() {
        val repository = SamrRepository()
        val initialPost = repository.posts.value.first()
        val initialLiked = initialPost.isLiked
        val initialCount = initialPost.likesCount

        repository.toggleLike(initialPost.id)
        val updatedPost = repository.posts.value.first { it.id == initialPost.id }

        assertEquals(!initialLiked, updatedPost.isLiked)
        if (!initialLiked) {
            assertEquals(initialCount + 1, updatedPost.likesCount)
        } else {
            assertEquals(initialCount - 1, updatedPost.likesCount)
        }
    }

    @Test
    fun `samr repository supports quiet mode toggle`() {
        val repository = SamrRepository()
        assertFalse(repository.isQuietMode.value)

        repository.toggleQuietMode()
        assertTrue(repository.isQuietMode.value)

        repository.toggleQuietMode()
        assertFalse(repository.isQuietMode.value)
    }

    @Test
    fun `samr repository social layers and mood filtering state updates`() {
        val repository = SamrRepository()
        assertEquals(SocialLayer.PERSONAL, repository.activeLayer.value)

        repository.setSocialLayer(SocialLayer.CREATIVE)
        assertEquals(SocialLayer.CREATIVE, repository.activeLayer.value)

        repository.setMood(MoodType.RELAX)
        assertEquals(MoodType.RELAX, repository.activeMood.value)
    }

    @Test
    fun `publishing post adds to feed stream`() {
        val repository = SamrRepository()
        val initialCount = repository.posts.value.size

        repository.publishPost(
            text = "Testing architectural synthesis in SAMR.",
            mediaUrls = emptyList(),
            circle = null,
            lifetime = PostLifetime.PERMANENT,
            collaborator = null
        )

        assertEquals(initialCount + 1, repository.posts.value.size)
        val newestPost = repository.posts.value.first()
        assertEquals("Testing architectural synthesis in SAMR.", newestPost.text)
    }
}
