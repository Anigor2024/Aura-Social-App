package com.samr.social

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.samr.social.core.model.MediaAsset
import com.samr.social.core.model.MediaKind
import com.samr.social.core.model.MediaOrigin
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

    @Test
    fun `media library supports favorite duplicate and delete`() {
        val repository = SamrRepository()
        val asset = MediaAsset(
            id = "test_media",
            uri = "content://samr/test-image",
            kind = MediaKind.IMAGE,
            origin = MediaOrigin.STUDIO,
            title = "Test image"
        )

        repository.addMediaAsset(asset)
        assertEquals(1, repository.mediaLibrary.value.size)

        repository.toggleMediaFavorite(asset.id)
        assertTrue(repository.mediaLibrary.value.first().isFavorite)

        repository.duplicateMediaAsset(asset.id)
        assertEquals(2, repository.mediaLibrary.value.size)

        repository.deleteMediaAsset(asset.id)
        assertEquals(1, repository.mediaLibrary.value.size)
    }

    @Test
    fun `media assets publish inside normal posts`() {
        val repository = SamrRepository()
        val asset = MediaAsset(
            id = "video_test",
            uri = "content://samr/test-video",
            kind = MediaKind.VIDEO,
            origin = MediaOrigin.CAMERA,
            title = "Test video",
            playbackSpeed = 1.5f,
            isMuted = true
        )

        repository.publishPost(
            text = "Media post",
            mediaUrls = emptyList(),
            circle = null,
            lifetime = PostLifetime.PERMANENT,
            collaborator = null,
            mediaAssets = listOf(asset)
        )

        val post = repository.posts.value.first()
        assertEquals(1, post.mediaAssets.size)
        assertEquals(MediaKind.VIDEO, post.mediaAssets.first().kind)
        assertTrue(post.mediaAssets.first().isMuted)
    }

    @Test
    fun `studio image or video can publish as story`() {
        val repository = SamrRepository()
        val initialCount = repository.stories.value.size
        val asset = MediaAsset(
            id = "story_asset",
            uri = "content://samr/story",
            kind = MediaKind.VIDEO,
            origin = MediaOrigin.CAMERA,
            title = "Studio story"
        )

        repository.publishAssetAsStory(asset, "New story")

        assertEquals(initialCount + 1, repository.stories.value.size)
        assertEquals(MediaKind.VIDEO, repository.stories.value.first().mediaKind)
        assertEquals("New story", repository.stories.value.first().caption)
    }

    @Test
    fun `media asset can be sent inside a conversation`() {
        val repository = SamrRepository()
        val asset = MediaAsset(
            id = "chat_asset",
            uri = "content://samr/chat-image",
            kind = MediaKind.IMAGE,
            origin = MediaOrigin.IMPORTED,
            title = "Chat image"
        )

        repository.sendMediaMessage(
            conversationId = "conv_aziz",
            asset = asset,
            caption = "شوف الصورة دي"
        )

        val message = repository.threadMessages.value.last()
        assertEquals(asset.id, message.mediaAsset?.id)
        assertEquals("شوف الصورة دي", message.text)
        assertTrue(message.isMine)
    }
}
