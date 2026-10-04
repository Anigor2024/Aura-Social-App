package com.samr.social.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_posts")
data class CachedPostEntity(
    @PrimaryKey
    val id: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String,
    val text: String,
    val mediaUrl: String?,
    val timestamp: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val socialLayer: String,
    val mood: String
)
