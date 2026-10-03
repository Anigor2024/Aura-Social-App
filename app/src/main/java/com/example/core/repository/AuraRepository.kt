package com.example.core.repository

import com.example.core.model.AuraCircle
import com.example.core.model.CatchUpSummary
import com.example.core.model.Clip
import com.example.core.model.Community
import com.example.core.model.Conversation
import com.example.core.model.DirectMessage
import com.example.core.model.MessageStatus
import com.example.core.model.MoodType
import com.example.core.model.Post
import com.example.core.model.PostLifetime
import com.example.core.model.SocialLayer
import com.example.core.model.Story
import com.example.core.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuraRepository {

    // Current User
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            username = "nour_arch",
            displayName = "Nour Al-Mansour",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=500&q=80",
            bio = "Architectural Designer & Spatial Theorist. Exploring minimalism, daylight dynamics, and sustainable structures.",
            isVerified = true,
            followersCount = 14200,
            followingCount = 428,
            postsCount = 86,
            activeSocialLayer = SocialLayer.PERSONAL
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Preferences & Modes
    private val _activeLayer = MutableStateFlow(SocialLayer.PERSONAL)
    val activeLayer: StateFlow<SocialLayer> = _activeLayer.asStateFlow()

    private val _activeMood = MutableStateFlow(MoodType.ALL)
    val activeMood: StateFlow<MoodType> = _activeMood.asStateFlow()

    private val _isQuietMode = MutableStateFlow(false)
    val isQuietMode: StateFlow<Boolean> = _isQuietMode.asStateFlow()

    // Circles
    private val _circles = MutableStateFlow(
        listOf(
            AuraCircle("circle_1", "Inner Circle", "Closest confidants and intimate updates", 0xFFE5B869, 6),
            AuraCircle("circle_2", "Design Studio", "Collaborators, sketches, and critiques", 0xFF8B5CF6, 18),
            AuraCircle("circle_3", "Tech Syndicate", "Android, systems engineering, and research", 0xFF38BDF8, 34),
            AuraCircle("circle_4", "Family & Kin", "Private family journal and memories", 0xFF10B981, 12)
        )
    )
    val circles: StateFlow<List<AuraCircle>> = _circles.asStateFlow()

    // Creators
    private val rami = User(
        id = "user_rami",
        username = "rami_tech",
        displayName = "Rami Haddad",
        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=500&q=80",
        bio = "Staff Distributed Systems Engineer | Crafting resilient cloud foundations & Kotlin runtimes.",
        isVerified = true,
        followersCount = 28400,
        followingCount = 312,
        isFollowing = true,
        activeSocialLayer = SocialLayer.TECH
    )

    private val elena = User(
        id = "user_elena",
        username = "elena_lens",
        displayName = "Elena Rostova",
        avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80",
        bio = "Analog & Editorial Photographer based between Berlin and Tokyo. Chasing shadows and golden hour.",
        isVerified = true,
        followersCount = 53100,
        followingCount = 189,
        isFollowing = true,
        activeSocialLayer = SocialLayer.CREATIVE
    )

    private val tariq = User(
        id = "user_tariq",
        username = "tariq_art",
        displayName = "Tariq Aziz",
        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80",
        bio = "Generative Sound Artist & Modular Synthesist. Composing ambient landscapes for quiet minds.",
        isVerified = false,
        followersCount = 9400,
        followingCount = 240,
        isFollowing = false,
        activeSocialLayer = SocialLayer.CREATIVE
    )

    private val sofia = User(
        id = "user_sofia",
        username = "sofia_nordic",
        displayName = "Sofia Lindholm",
        avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=500&q=80",
        bio = "Scandinavian interior designer and slow-living advocate. Light, cedar wood, and silent mornings.",
        isVerified = true,
        followersCount = 38900,
        followingCount = 410,
        isFollowing = true,
        activeSocialLayer = SocialLayer.PERSONAL
    )

    // Stories
    private val _stories = MutableStateFlow(
        listOf(
            Story(
                id = "story_me",
                author = _currentUser.value,
                mediaUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=600&q=80",
                timestampAgo = "2h ago",
                caption = "Morning daylight pouring over the cedar study model."
            ),
            Story(
                id = "story_1",
                author = elena,
                mediaUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=600&q=80",
                timestampAgo = "3h ago",
                caption = "35mm Kodachrome grain in Kyoto back alleys."
            ),
            Story(
                id = "story_2",
                author = rami,
                mediaUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=600&q=80",
                timestampAgo = "5h ago",
                caption = "Benchmarking zero-copy memory buffers."
            ),
            Story(
                id = "story_3",
                author = sofia,
                mediaUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=600&q=80",
                timestampAgo = "6h ago",
                caption = "The serenity of winter twilight in Oslo."
            )
        )
    )
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // Posts
    private val _posts = MutableStateFlow(
        listOf(
            Post(
                id = "post_1",
                author = elena,
                text = "Shadows in Kyoto. There is something sacred about how daylight carves geometry into aged cedar walls before dusk. Captured on medium format film. #architecture #filmphotography #kyoto",
                mediaUrls = listOf("https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=1200&q=80"),
                timestampFormatted = "18m ago",
                likesCount = 1842,
                commentsCount = 143,
                repostsCount = 89,
                isLiked = true,
                isBookmarked = true,
                socialLayer = SocialLayer.CREATIVE,
                mood = MoodType.RELAX,
                discussionRoomId = "room_kyoto_light"
            ),
            Post(
                id = "post_2",
                author = rami,
                text = "Announcing our collaborative architectural framework with @nour_arch. Merging physical pavilion acoustics with dynamic ambient sound generative synthesis. Built with high-fidelity low latency engines.",
                mediaUrls = listOf("https://images.unsplash.com/photo-1506157786151-b8491531f063?auto=format&fit=crop&w=1200&q=80"),
                timestampFormatted = "1h ago",
                likesCount = 3120,
                commentsCount = 284,
                repostsCount = 192,
                collaborator = _currentUser.value,
                socialLayer = SocialLayer.TECH,
                mood = MoodType.DISCOVER,
                discussionRoomId = "room_spatial_tech"
            ),
            Post(
                id = "post_3",
                author = _currentUser.value,
                text = "Sharing our latest study with the Inner Circle: Monolithic rammed-earth villa prototypes designed for extreme climate resilience and passive cooling in the desert plateau.",
                mediaUrls = listOf("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80"),
                timestampFormatted = "3h ago",
                likesCount = 420,
                commentsCount = 38,
                repostsCount = 12,
                circle = _circles.value[0], // Inner Circle
                socialLayer = SocialLayer.PERSONAL,
                mood = MoodType.FOCUS,
                lifetime = PostLifetime.HOURS_24
            ),
            Post(
                id = "post_4",
                author = sofia,
                text = "The art of subtraction. When you eliminate decorative noise from your living space, each object begins to breathe. What is the one possession in your room that brings you deepest calm?",
                mediaUrls = listOf("https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1200&q=80"),
                timestampFormatted = "5h ago",
                likesCount = 2450,
                commentsCount = 310,
                repostsCount = 76,
                socialLayer = SocialLayer.PERSONAL,
                mood = MoodType.RELAX
            ),
            Post(
                id = "post_5",
                author = tariq,
                text = "Exploring generative micro-tonal textures inspired by rainfall frequencies. No synthesizers were tuned to standard 440Hz during this session. Listen with headphones for spatial depth.",
                mediaUrls = listOf("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=1200&q=80"),
                timestampFormatted = "8h ago",
                likesCount = 980,
                commentsCount = 67,
                repostsCount = 43,
                socialLayer = SocialLayer.CREATIVE,
                mood = MoodType.LEARN,
                discussionRoomId = "room_ambient_frequencies"
            )
        )
    )
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Clips
    private val _clips = MutableStateFlow(
        listOf(
            Clip(
                id = "clip_1",
                author = elena,
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-tree-branches-in-the-breeze-1188-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=600&q=80",
                caption = "Golden hour reflections dancing through traditional shoji screens in Kyoto. Pure stillness.",
                audioTrackTitle = "Kitsuné Ambient - Rain Over Kyoto",
                likesCount = 14200,
                commentsCount = 890,
                isLiked = false
            ),
            Clip(
                id = "clip_2",
                author = rami,
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-hands-typing-on-a-laptop-42999-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=600&q=80",
                caption = "Zero latency reactive graphics rendering in 120 FPS on Kotlin Native. The smoothness is unreal.",
                audioTrackTitle = "Synthesizer Echoes - Deep Focus",
                likesCount = 28300,
                commentsCount = 1420,
                isLiked = true
            ),
            Clip(
                id = "clip_3",
                author = tariq,
                videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-dj-mixing-music-in-a-club-41712-large.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80",
                caption = "Live modular patch recording session. Voltage controlled filters modulating in real-time.",
                audioTrackTitle = "Tariq Aziz - Modular Pulse Vol. 3",
                likesCount = 9450,
                commentsCount = 412,
                isLiked = false
            )
        )
    )
    val clips: StateFlow<List<Clip>> = _clips.asStateFlow()

    // Conversations & Messages
    private val _conversations = MutableStateFlow(
        listOf(
            Conversation(
                id = "conv_rami",
                participant = rami,
                lastMessage = "Sent the audio spectrogram files for the acoustic pavilion review.",
                lastTimestamp = "14:28",
                unreadCount = 2,
                isOnline = true
            ),
            Conversation(
                id = "conv_elena",
                participant = elena,
                lastMessage = "Voice note (0:38)",
                lastTimestamp = "Yesterday",
                unreadCount = 0,
                isOnline = false
            ),
            Conversation(
                id = "conv_circle_studio",
                participant = User(
                    id = "group_studio",
                    username = "design_studio",
                    displayName = "Design Studio Circle",
                    avatarUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=500&q=80",
                    bio = "Private circle workspace for project critiques"
                ),
                lastMessage = "Tariq: The new acoustic panel simulations are ready for download.",
                lastTimestamp = "Yesterday",
                unreadCount = 0,
                isCircleChat = true,
                circleName = "Design Studio"
            )
        )
    )
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Direct Messages Thread for Rami
    private val _threadMessages = MutableStateFlow(
        listOf(
            DirectMessage(
                id = "msg_1",
                conversationId = "conv_rami",
                senderId = "user_rami",
                text = "Hey Nour, the acoustic modeling benchmarks for the pavilion are surpassing initial predictions.",
                timestampFormatted = "14:10",
                isMine = false,
                status = MessageStatus.READ
            ),
            DirectMessage(
                id = "msg_2",
                conversationId = "conv_rami",
                senderId = "user_me",
                text = "That is incredible news Rami! Are the low frequencies dampening properly against the curved rammed earth walls?",
                timestampFormatted = "14:15",
                isMine = true,
                status = MessageStatus.READ
            ),
            DirectMessage(
                id = "msg_3",
                conversationId = "conv_rami",
                senderId = "user_rami",
                text = "Listen to this preview simulation recorded at the focal point:",
                timestampFormatted = "14:20",
                isMine = false,
                voiceDurationSeconds = 34,
                voiceWaveform = listOf(0.2f, 0.4f, 0.8f, 0.9f, 0.6f, 0.3f, 0.7f, 1f, 0.5f, 0.3f, 0.7f, 0.4f, 0.2f),
                status = MessageStatus.READ
            ),
            DirectMessage(
                id = "msg_4",
                conversationId = "conv_rami",
                senderId = "user_rami",
                text = "Sent the audio spectrogram files for the acoustic pavilion review.",
                timestampFormatted = "14:28",
                isMine = false,
                status = MessageStatus.DELIVERED
            )
        )
    )
    val threadMessages: StateFlow<List<DirectMessage>> = _threadMessages.asStateFlow()

    // Communities
    private val _communities = MutableStateFlow(
        listOf(
            Community(
                id = "comm_arch",
                name = "Architects of Tomorrow",
                description = "Exploring sustainable materials, rammed earth, kinetic facades, and light architecture.",
                avatarUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=500&q=80",
                coverUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1200&q=80",
                membersCountFormatted = "48.2k",
                isJoined = true,
                category = "Architecture & Spatial",
                rules = listOf("Focus on constructive critiques", "Credit photographers and structural engineers")
            ),
            Community(
                id = "comm_photo",
                name = "Analog & Editorial Visuals",
                description = "Dedicated to medium format film, golden hour shadows, and slow visual storytelling.",
                avatarUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=500&q=80",
                coverUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=80",
                membersCountFormatted = "92.5k",
                isJoined = false,
                category = "Creative Arts"
            ),
            Community(
                id = "comm_tech",
                name = "Next-Gen Android Engineering",
                description = "Discussions on Jetpack Compose, Kotlin Multiplatform, low-latency media, and UI architectures.",
                avatarUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=500&q=80",
                coverUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80",
                membersCountFormatted = "64.1k",
                isJoined = true,
                category = "Technology"
            )
        )
    )
    val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    // Smart Catch-up Snapshot
    val catchUpSummary = CatchUpSummary(
        missedPostsCount = 14,
        topDiscussions = _posts.value.take(2),
        circleUpdatesCount = 2,
        unreadMessagesCount = 2
    )

    // Mutation Actions (Optimistic UI)
    fun toggleLike(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newCount = if (newLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
                post.copy(isLiked = newLiked, likesCount = newCount)
            } else post
        }
    }

    fun toggleBookmark(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(isBookmarked = !post.isBookmarked) else post
        }
    }

    fun toggleRepost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(repostsCount = post.repostsCount + 1)
            } else post
        }
    }

    fun toggleFollow(userId: String) {
        // Optimistic toggle
    }

    fun setSocialLayer(layer: SocialLayer) {
        _activeLayer.value = layer
    }

    fun setMood(mood: MoodType) {
        _activeMood.value = mood
    }

    fun toggleQuietMode() {
        _isQuietMode.value = !_isQuietMode.value
    }

    fun publishPost(
        text: String,
        mediaUrls: List<String>,
        circle: AuraCircle?,
        lifetime: PostLifetime,
        collaborator: User?
    ) {
        val newPost = Post(
            id = "post_${UUID.randomUUID().toString().take(8)}",
            author = _currentUser.value,
            text = text,
            mediaUrls = mediaUrls,
            timestampFormatted = "Just now",
            likesCount = 0,
            commentsCount = 0,
            repostsCount = 0,
            isLiked = false,
            isBookmarked = false,
            circle = circle,
            socialLayer = _activeLayer.value,
            mood = _activeMood.value,
            lifetime = lifetime,
            collaborator = collaborator
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    fun sendMessage(conversationId: String, text: String, voiceDuration: Int? = null) {
        val newMsg = DirectMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = "user_me",
            text = text,
            timestampFormatted = "Just now",
            isMine = true,
            voiceDurationSeconds = voiceDuration,
            voiceWaveform = if (voiceDuration != null) listOf(0.3f, 0.7f, 0.9f, 0.4f, 0.8f, 0.5f, 0.2f) else null,
            status = MessageStatus.SENT
        )
        _threadMessages.value = _threadMessages.value + newMsg
    }

    fun toggleJoinCommunity(communityId: String) {
        _communities.value = _communities.value.map {
            if (it.id == communityId) it.copy(isJoined = !it.isJoined) else it
        }
    }

    fun toggleClipLike(clipId: String) {
        _clips.value = _clips.value.map { clip ->
            if (clip.id == clipId) {
                val newLiked = !clip.isLiked
                val newCount = if (newLiked) clip.likesCount + 1 else maxOf(0, clip.likesCount - 1)
                clip.copy(isLiked = newLiked, likesCount = newCount)
            } else clip
        }
    }
}
