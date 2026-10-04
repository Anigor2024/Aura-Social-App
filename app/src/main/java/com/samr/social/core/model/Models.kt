package com.samr.social.core.model

enum class SocialLayer(val id: String, val titleResKey: String) {
    PERSONAL("personal", "social_layer_personal"),
    PROFESSIONAL("professional", "social_layer_professional"),
    CREATIVE("creative", "social_layer_creative"),
    TECH("tech", "social_layer_tech")
}

enum class MoodType(val id: String, val titleResKey: String) {
    ALL("all", "feed_for_you"),
    RELAX("relax", "mood_relax"),
    DISCOVER("discover", "mood_discover"),
    LEARN("learn", "mood_learn"),
    LAUGH("laugh", "mood_laugh"),
    CONNECT("connect", "mood_connect"),
    FOCUS("focus", "mood_focus")
}

data class SamrCircle(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val description: String,
    val colorHex: Long,
    val memberCount: Int
) {
    fun localizedName(isArabic: Boolean): String = if (isArabic) nameAr else nameEn
}

data class ProfileLink(
    val label: String,
    val value: String
)

data class UserAchievement(
    val id: String,
    val title: String,
    val icon: String,
    val description: String
)

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bio: String,
    val location: String = "Riyadh, Saudi Arabia",
    val isVerified: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val isFollowing: Boolean = false,
    val activeSocialLayer: SocialLayer = SocialLayer.PERSONAL,
    val profileLinks: List<ProfileLink> = emptyList(),
    val achievements: List<UserAchievement> = emptyList()
)

enum class PostLifetime(val id: String, val hours: Int) {
    PERMANENT("permanent", -1),
    HOURS_24("24h", 24),
    DAYS_3("3d", 72),
    DAYS_7("7d", 168)
}

data class PollOption(
    val id: String,
    val text: String,
    val votes: Int = 0
)

data class PostPoll(
    val question: String,
    val options: List<PollOption>,
    val selectedOptionId: String? = null,
    val isClosed: Boolean = false
) {
    val totalVotes: Int
        get() = options.sumOf { it.votes }
}

enum class MediaKind { IMAGE, VIDEO, AUDIO }

enum class MediaOrigin { IMPORTED, CAMERA, RECORDER, STUDIO }

data class MediaAsset(
    val id: String,
    val uri: String,
    val kind: MediaKind,
    val origin: MediaOrigin,
    val title: String,
    val mimeType: String = "",
    val durationMs: Long = 0L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long? = null,
    val playbackSpeed: Float = 1f,
    val isMuted: Boolean = false,
    val filterName: String = "Original",
    val overlayText: String = "",
    val altText: String = "",
    val isFavorite: Boolean = false
)

data class Post(
    val id: String,
    val author: User,
    val text: String,
    val mediaUrls: List<String> = emptyList(),
    val mediaAssets: List<MediaAsset> = emptyList(),
    val timestampMinutesAgo: Int = 10,
    val likesCount: Int,
    val commentsCount: Int,
    val repostsCount: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val circle: SamrCircle? = null,
    val socialLayer: SocialLayer = SocialLayer.PERSONAL,
    val mood: MoodType = MoodType.DISCOVER,
    val lifetime: PostLifetime = PostLifetime.PERMANENT,
    val collaborator: User? = null,
    val discussionRoomId: String? = null,
    val discussionRoomTopic: String? = null,
    val isQuietModeEligible: Boolean = true,
    val allowComments: Boolean = true,
    val hideLikeCount: Boolean = false,
    val isPinned: Boolean = false,
    val resonanceCount: Int = 0,
    val isResonated: Boolean = false,
    val poll: PostPoll? = null,
    val locationTag: String? = null,
    val altText: String? = null,
    val isReported: Boolean = false,
    val isHidden: Boolean = false
)

data class PostComment(
    val id: String,
    val postId: String,
    val author: User,
    val text: String,
    val timestampLabel: String = "الآن",
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, MENTION, MESSAGE, COMMUNITY, SYSTEM
}

data class SocialNotification(
    val id: String,
    val actor: User?,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timestampLabel: String,
    val isRead: Boolean = false
)

data class SavedCollection(
    val id: String,
    val title: String,
    val postIds: List<String> = emptyList()
)

data class ExperiencePreferences(
    val autoplayVideos: Boolean = true,
    val reducedMotion: Boolean = false,
    val compactFeed: Boolean = false,
    val hapticFeedback: Boolean = true,
    val highQualityMedia: Boolean = true,
    val showReadReceipts: Boolean = true
)

data class PostDraft(
    val id: String,
    val text: String,
    val mediaUrl: String? = null,
    val mediaAssets: List<MediaAsset> = emptyList(),
    val locationTag: String = "",
    val altText: String = "",
    val updatedLabel: String = "الآن"
)

data class ScheduledPost(
    val id: String,
    val text: String,
    val mediaUrl: String? = null,
    val mediaAssets: List<MediaAsset> = emptyList(),
    val scheduledLabel: String,
    val locationTag: String = "",
    val altText: String = ""
)

data class PrivacyPreferences(
    val allowMessages: Boolean = true,
    val allowMentions: Boolean = true,
    val showActivityStatus: Boolean = true,
    val sensitiveContentFilter: Boolean = true,
    val allowMediaDownloads: Boolean = true,
    val allowRemixes: Boolean = true,
    val allowClipReuse: Boolean = true,
    val hiddenWords: List<String> = listOf("spam", "spoiler")
)

data class EchoNote(
    val id: String,
    val author: User,
    val text: String,
    val emoji: String = "✦",
    val timestampLabel: String = "الآن",
    val isMine: Boolean = false
)

data class Story(
    val id: String,
    val author: User,
    val mediaUrl: String,
    val timestampMinutesAgo: Int = 120,
    val isViewed: Boolean = false,
    val caption: String = ""
)

data class ClipComment(
    val id: String,
    val clipId: String,
    val author: User,
    val text: String,
    val timestampLabel: String = "الآن"
)

data class Clip(
    val id: String,
    val author: User,
    val videoUrl: String,
    val thumbnailUrl: String,
    val caption: String,
    val audioTrackTitle: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

data class DirectMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestampFormatted: String,
    val isMine: Boolean,
    val voiceDurationSeconds: Int? = null,
    val voiceWaveform: List<Float>? = null,
    val mediaUrl: String? = null,
    val status: MessageStatus = MessageStatus.READ,
    val reaction: String? = null,
    val replyToText: String? = null
)

data class Conversation(
    val id: String,
    val participant: User,
    val lastMessage: String,
    val lastTimestamp: String,
    val unreadCount: Int = 0,
    val isCircleChat: Boolean = false,
    val circleName: String? = null,
    val isOnline: Boolean = false,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false
)

enum class MajlisStatus {
    LIVE, UPCOMING, ENDED
}

data class MajlisRoom(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val host: User,
    val coHosts: List<User> = emptyList(),
    val status: MajlisStatus,
    val participantCount: Int,
    val scheduledLabel: String,
    val accentHex: Long = 0xFFD4AF37,
    val isJoined: Boolean = false,
    val isHandRaised: Boolean = false,
    val isReminderSet: Boolean = false,
    val applauseCount: Int = 0,
    val heartCount: Int = 0
)

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String,
    val coverUrl: String,
    val membersCount: Int = 1000,
    val isJoined: Boolean = false,
    val category: String,
    val rules: List<String> = emptyList()
)

data class CatchUpSummary(
    val missedPostsCount: Int,
    val topDiscussions: List<Post>,
    val circleUpdatesCount: Int,
    val unreadMessagesCount: Int
)
