package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.model.MoodType
import com.example.core.model.PostLifetime
import com.example.core.model.SocialLayer
import com.example.core.repository.AuraRepository
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
    fun `read string from context verifies Aura brand name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aura", appName)
    }

    @Test
    fun `aura repository optimistic like toggle updates count correctly`() {
        val repository = AuraRepository()
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
    fun `aura repository supports quiet mode toggle`() {
        val repository = AuraRepository()
        assertFalse(repository.isQuietMode.value)

        repository.toggleQuietMode()
        assertTrue(repository.isQuietMode.value)

        repository.toggleQuietMode()
        assertFalse(repository.isQuietMode.value)
    }

    @Test
    fun `aura repository social layers and mood filtering state updates`() {
        val repository = AuraRepository()
        assertEquals(SocialLayer.PERSONAL, repository.activeLayer.value)

        repository.setSocialLayer(SocialLayer.CREATIVE)
        assertEquals(SocialLayer.CREATIVE, repository.activeLayer.value)

        repository.setMood(MoodType.RELAX)
        assertEquals(MoodType.RELAX, repository.activeMood.value)
    }

    @Test
    fun `publishing post adds to feed stream`() {
        val repository = AuraRepository()
        val initialCount = repository.posts.value.size

        repository.publishPost(
            text = "Testing architectural synthesis in Aura.",
            mediaUrls = emptyList(),
            circle = null,
            lifetime = PostLifetime.PERMANENT,
            collaborator = null
        )

        assertEquals(initialCount + 1, repository.posts.value.size)
        val newestPost = repository.posts.value.first()
        assertEquals("Testing architectural synthesis in Aura.", newestPost.text)
    }
}
