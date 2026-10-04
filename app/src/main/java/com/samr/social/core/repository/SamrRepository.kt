package com.samr.social.core.repository

import android.content.Context
import com.samr.social.core.model.CatchUpSummary
import com.samr.social.core.model.Clip
import com.samr.social.core.model.ClipComment
import com.samr.social.core.model.EchoNote
import com.samr.social.core.model.ExperiencePreferences
import com.samr.social.core.model.Community
import com.samr.social.core.model.Conversation
import com.samr.social.core.model.DirectMessage
import com.samr.social.core.model.MajlisRoom
import com.samr.social.core.model.MajlisStatus
import com.samr.social.core.model.MediaAsset
import com.samr.social.core.model.MediaKind
import com.samr.social.core.model.MediaOrigin
import com.samr.social.core.model.MessageStatus
import com.samr.social.core.model.MoodType
import com.samr.social.core.model.PollOption
import com.samr.social.core.model.Post
import com.samr.social.core.model.PostComment
import com.samr.social.core.model.PostLifetime
import com.samr.social.core.model.PostDraft
import com.samr.social.core.model.PostPoll
import com.samr.social.core.model.PrivacyPreferences
import com.samr.social.core.model.ProfileLink
import com.samr.social.core.model.SamrCircle
import com.samr.social.core.model.SavedCollection
import com.samr.social.core.model.ScheduledPost
import com.samr.social.core.model.SocialLayer
import com.samr.social.core.model.SocialNotification
import com.samr.social.core.model.NotificationType
import com.samr.social.core.model.Story
import com.samr.social.core.model.User
import com.samr.social.core.model.UserAchievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

class SamrRepository(
    private val appContext: Context? = null
) {
    private val mediaPrefs =
        appContext?.getSharedPreferences("samr_media_library", Context.MODE_PRIVATE)

    // Current User Profile (Noura Al-Otaibi - Saudi Product Designer)
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_noura",
            username = "noura.design",
            displayName = "نورة العتيبي",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=500&q=80",
            bio = "مصممة منتجات رقمية من الرياض. مهتمة بالتجارب الهادئة والتفاصيل الصغيرة التي تصنع فرقًا كبيرًا.",
            location = "الرياض، المملكة العربية السعودية",
            isVerified = true,
            followersCount = 18400,
            followingCount = 385,
            postsCount = 94,
            activeSocialLayer = SocialLayer.PERSONAL,
            profileLinks = listOf(
                ProfileLink("Portfolio", "noura.design/work"),
                ProfileLink("Behance", "behance.net/noura"),
                ProfileLink("LinkedIn", "linkedin.com/in/noura")
            ),
            achievements = listOf(
                UserAchievement("ach_founder", "صوت مؤثر", "✦", "تفاعل نوعي ومستمر مع المجتمع"),
                UserAchievement("ach_design", "رؤية تصميمية", "◈", "محتوى مميز في التصميم والمنتج"),
                UserAchievement("ach_community", "صانعة مجتمع", "◎", "مساهمات فعالة داخل الدوائر والمجالس")
            )
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Mode States
    private val _activeLayer = MutableStateFlow(SocialLayer.PERSONAL)
    val activeLayer: StateFlow<SocialLayer> = _activeLayer.asStateFlow()

    private val _activeMood = MutableStateFlow(MoodType.ALL)
    val activeMood: StateFlow<MoodType> = _activeMood.asStateFlow()

    private val _isQuietMode = MutableStateFlow(false)
    val isQuietMode: StateFlow<Boolean> = _isQuietMode.asStateFlow()

    // Circles (الدوائر)
    private val _circles = MutableStateFlow(
        listOf(
            SamrCircle(
                id = "circle_inner",
                nameEn = "Inner Circle",
                nameAr = "المقرّبون",
                description = "أحاديث شخصية وتأملات خاصة",
                colorHex = 0xFFD4AF37,
                memberCount = 8
            ),
            SamrCircle(
                id = "circle_design",
                nameEn = "Design Studio",
                nameAr = "استوديو التصميم",
                description = "مراجعات التصميم وتجارب الواجهات",
                colorHex = 0xFF8B5CF6,
                memberCount = 22
            ),
            SamrCircle(
                id = "circle_tech",
                nameEn = "Tech Syndicate",
                nameAr = "مجتمع التقنية",
                description = "نقاشات هندسة أندرويد والحوسبة",
                colorHex = 0xFF10B981,
                memberCount = 45
            ),
            SamrCircle(
                id = "circle_family",
                nameEn = "Family & Friends",
                nameAr = "العائلة والأصدقاء",
                description = "جلسات عائلية ولحظات أسرية",
                colorHex = 0xFF38BDF8,
                memberCount = 14
            )
        )
    )
    val circles: StateFlow<List<SamrCircle>> = _circles.asStateFlow()

    // Key Content Creators
    private val aziz = User(
        id = "user_aziz",
        username = "aziz.dev",
        displayName = "عبدالعزيز الحربي",
        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=500&q=80",
        bio = "مهندس برمجيات أندرويد • الرياض | Kotlin & Jetpack Compose. أبحث عن الأداء العالي والبساطة في الأنظمة المعقدة.",
        location = "الرياض، المملكة العربية السعودية",
        isVerified = true,
        followersCount = 32500,
        followingCount = 260,
        isFollowing = true,
        activeSocialLayer = SocialLayer.TECH
    )

    private val faisal = User(
        id = "user_faisal",
        username = "faisal.photo",
        displayName = "فيصل الرشيد",
        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80",
        bio = "مصور معماري وفوتوغرافي من جدة. أوثّق الضوء والظلال في جدة التاريخية وتفاصيل العلا.",
        location = "جدة، المملكة العربية السعودية",
        isVerified = true,
        followersCount = 48200,
        followingCount = 190,
        isFollowing = true,
        activeSocialLayer = SocialLayer.CREATIVE
    )

    private val reem = User(
        id = "user_reem",
        username = "reem.space",
        displayName = "ريم السبيعي",
        avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=500&q=80",
        bio = "معمارية وباحثة في تقنيات البناء المستدام ومواد الطين الحديثة. الظهران • الدرعية.",
        location = "الدرعية، المملكة العربية السعودية",
        isVerified = true,
        followersCount = 26700,
        followingCount = 310,
        isFollowing = true,
        activeSocialLayer = SocialLayer.PROFESSIONAL
    )

    private val rami = User(
        id = "user_rami",
        username = "rami.tech",
        displayName = "رامي الحداد",
        avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=500&q=80",
        bio = "Architect of distributed systems & ambient audio synthesis. Dubai • Beirut.",
        location = "Dubai, UAE",
        isVerified = true,
        followersCount = 19800,
        followingCount = 145,
        isFollowing = false,
        activeSocialLayer = SocialLayer.TECH
    )

    private val elena = User(
        id = "user_elena",
        username = "elena.lens",
        displayName = "Elena Rostova",
        avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80",
        bio = "Analog & Medium Format Editorial Photographer. Chasing golden hour between Berlin & Tokyo.",
        location = "Berlin, Germany",
        isVerified = true,
        followersCount = 61200,
        followingCount = 180,
        isFollowing = true,
        activeSocialLayer = SocialLayer.CREATIVE
    )

    // Stories (القصص)
    private val _stories = MutableStateFlow(
        listOf(
            Story(
                id = "story_noura",
                author = _currentUser.value,
                mediaUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=600&q=80",
                timestampMinutesAgo = 45,
                caption = "صباح الخير من مكتب التصميم في الرياض. استعراض النموذج الأولي للواجهات."
            ),
            Story(
                id = "story_faisal",
                author = faisal,
                mediaUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=600&q=80",
                timestampMinutesAgo = 120,
                caption = "انعكاس خيوط الشمس الذهبية في أزقة الدرعية القديمة."
            ),
            Story(
                id = "story_aziz",
                author = aziz,
                mediaUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=600&q=80",
                timestampMinutesAgo = 180,
                caption = "تجربة أداء Compose 1.8 مع الرسوم التفاعلية المباشرة."
            ),
            Story(
                id = "story_reem",
                author = reem,
                mediaUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=600&q=80",
                timestampMinutesAgo = 240,
                caption = "عينات الطين المعالج لعزل الحرارة في المشاريع المستدامة."
            ),
            Story(
                id = "story_elena",
                author = elena,
                mediaUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=600&q=80",
                timestampMinutesAgo = 360,
                caption = "Film grain studies from modern Japanese pavilions."
            )
        )
    )
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _echoNotes = MutableStateFlow(
        listOf(
            EchoNote(
                id = "echo_noura",
                author = _currentUser.value,
                text = "أختبر تفاصيل واجهة جديدة اليوم ✦",
                emoji = "✦",
                timestampLabel = "الآن",
                isMine = true
            ),
            EchoNote(
                id = "echo_aziz",
                author = aziz,
                text = "Compose + قهوة = تركيز",
                emoji = "☕",
                timestampLabel = "18 د"
            ),
            EchoNote(
                id = "echo_faisal",
                author = faisal,
                text = "الضوء في العلا اليوم استثنائي",
                emoji = "☀",
                timestampLabel = "42 د"
            ),
            EchoNote(
                id = "echo_reem",
                author = reem,
                text = "أبحث عن خامات مستدامة جديدة",
                emoji = "◌",
                timestampLabel = "1 س"
            )
        )
    )
    val echoNotes: StateFlow<List<EchoNote>> = _echoNotes.asStateFlow()

    // Posts (المنشورات الاجتماعية الواقعية)
    private val _posts = MutableStateFlow(
        listOf(
            Post(
                id = "post_noura_1",
                author = _currentUser.value,
                text = "جربنا اليوم النسخة الجديدة من تجربة التنقل داخل المنتج. تغيير بسيط في ترتيب العناصر قلّل عدد الخطوات بنسبة 40%. أحيانًا أفضل قرارات الـ Product Design هي الأشياء التي لا يلاحظ المستخدم وجودها أصلًا لأنها تعمل بانسيابية طبيعية. #تصميم #تجربة_المستخدم #الرياض",
                mediaUrls = listOf("https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?auto=format&fit=crop&w=1200&q=80"),
                timestampMinutesAgo = 18,
                likesCount = 2420,
                commentsCount = 186,
                repostsCount = 94,
                isLiked = true,
                isBookmarked = true,
                socialLayer = SocialLayer.PROFESSIONAL,
                mood = MoodType.DISCOVER,
                discussionRoomId = "room_ux_simplicity",
                discussionRoomTopic = "بساطة تجربة المستخدم"
            ),
            Post(
                id = "post_faisal_1",
                author = faisal,
                text = "ضوء آخر العصر في الدرعية له طابع مختلف تمامًا. تدرجات الشمس الذهبية على جدران الطين العريقة تعطي إحساسًا بالدفء والسكينة. هذه اللقطة من تجربة عدسة 50mm الجديدة، بدون معالجة ألوان إضافية سوى توازن الإضاءة الطبيعية. #الدرعية #تصوير #العمارة",
                mediaUrls = listOf("https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=1200&q=80"),
                timestampMinutesAgo = 55,
                likesCount = 3890,
                commentsCount = 270,
                repostsCount = 142,
                socialLayer = SocialLayer.CREATIVE,
                mood = MoodType.RELAX
            ),
            Post(
                id = "post_aziz_1",
                author = aziz,
                text = "تطوير تطبيقات Android الحديثة مع Jetpack Compose أصبح ممتعًا للغاية، لكن في المشاريع الكبيرة يجب أن ينطلق القرار الهندسي من متطلبات المنتج الفعلية وليس مجرد الانجذاب لأحدث التقنيات. تجنب التعقيد غير المبرر هو قمة النضج الهندسي. #تقنية #تطوير_التطبيقات #Android",
                mediaUrls = listOf("https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=1200&q=80"),
                timestampMinutesAgo = 130,
                likesCount = 4120,
                commentsCount = 318,
                repostsCount = 210,
                socialLayer = SocialLayer.TECH,
                mood = MoodType.LEARN,
                discussionRoomId = "room_android_architecture",
                discussionRoomTopic = "معمارية تطبيقات Android",
                poll = PostPoll(
                    question = "أي جانب يحتاج أكبر تركيز في تطبيق اجتماعي عربي جديد؟",
                    options = listOf(
                        PollOption("poll_arch_1", "الخصوصية والتحكم", 684),
                        PollOption("poll_arch_2", "سرعة وسلاسة التجربة", 932),
                        PollOption("poll_arch_3", "المجتمعات والمحتوى", 511)
                    )
                )
            ),
            Post(
                id = "post_reem_collab",
                author = reem,
                text = "بالتعاون مع @noura.design في تصميم الجناح التفاعلي الجديد: دمج مواد البناء المحلية الصديقة للبيئة مع إضاءة ديناميكية تستجيب لحركة الزوار. الهندسة المعمارية ليست مجرد كتل خرسانية، بل مساحات حوارية حية تلهم الحواس. #العمارة #تصميم #مستقبل_المدن",
                mediaUrls = listOf("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80"),
                timestampMinutesAgo = 280,
                likesCount = 3210,
                commentsCount = 194,
                repostsCount = 118,
                collaborator = _currentUser.value,
                socialLayer = SocialLayer.PROFESSIONAL,
                mood = MoodType.DISCOVER
            ),
            Post(
                id = "post_circle_private",
                author = _currentUser.value,
                text = "أشارككم في دائرة المقربين مسودة التوجه البصري لمنصة سَمَر. الهدف تقديم هوية رقمية عربية معاصرة تنافس عالميًا وتفخر بجذورها الثقافية دون تكلف.",
                mediaUrls = listOf("https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1200&q=80"),
                timestampMinutesAgo = 420,
                likesCount = 530,
                commentsCount = 44,
                repostsCount = 12,
                circle = _circles.value[0], // المقربون
                socialLayer = SocialLayer.PERSONAL,
                mood = MoodType.FOCUS,
                lifetime = PostLifetime.HOURS_24
            ),
            Post(
                id = "post_elena_1",
                author = elena,
                text = "Afternoon shadows cutting through the minimalist pavilions in Kyoto. There is an unmistakable serenity in the cadence of aged cedarwood and filtered sunlight before twilight. Captured on 35mm film. #filmphotography #architecture #travel",
                mediaUrls = listOf("https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=1200&q=80"),
                timestampMinutesAgo = 520,
                likesCount = 2940,
                commentsCount = 188,
                repostsCount = 76,
                socialLayer = SocialLayer.CREATIVE,
                mood = MoodType.RELAX
            )
        )
    )
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _comments = MutableStateFlow(
        listOf(
            PostComment(
                id = "comment_1",
                postId = "post_noura_1",
                author = aziz,
                text = "الفرق واضح جدًا في ترتيب الخطوات. أكثر شيء أعجبني هو بساطة القرار بدون التضحية بالوضوح.",
                timestampLabel = "12 د"
            ),
            PostComment(
                id = "comment_2",
                postId = "post_noura_1",
                author = reem,
                text = "هذا النوع من التحسينات هو الذي يجعل المنتج يشعر بأنه طبيعي بدل أن يكون مجرد واجهة جميلة.",
                timestampLabel = "8 د",
                likesCount = 14
            )
        )
    )
    val comments: StateFlow<List<PostComment>> = _comments.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            SocialNotification(
                id = "notif_1",
                actor = aziz,
                type = NotificationType.COMMENT,
                title = "تعليق جديد",
                body = "عبدالعزيز علّق على منشورك عن تجربة المستخدم.",
                timestampLabel = "منذ 8 دقائق"
            ),
            SocialNotification(
                id = "notif_2",
                actor = faisal,
                type = NotificationType.LIKE,
                title = "تفاعل جديد",
                body = "فيصل و 128 شخصًا أعجبوا بمنشورك الأخير.",
                timestampLabel = "منذ 25 دقيقة"
            ),
            SocialNotification(
                id = "notif_3",
                actor = reem,
                type = NotificationType.MENTION,
                title = "تمت الإشارة إليك",
                body = "ريم أشارت إليك في منشور تعاوني جديد.",
                timestampLabel = "منذ ساعة"
            ),
            SocialNotification(
                id = "notif_4",
                actor = null,
                type = NotificationType.SYSTEM,
                title = "استدراك ذكي",
                body = "لديك 12 تحديثًا مهمًا من دوائرك ومجتمعاتك.",
                timestampLabel = "اليوم"
            )
        )
    )
    val notifications: StateFlow<List<SocialNotification>> = _notifications.asStateFlow()

    private val _savedCollections = MutableStateFlow(
        listOf(
            SavedCollection(
                id = "collection_inspiration",
                title = "إلهام",
                postIds = _posts.value.filter { it.isBookmarked }.map { it.id }
            ),
            SavedCollection(
                id = "collection_work",
                title = "للعمل لاحقًا"
            )
        )
    )
    val savedCollections: StateFlow<List<SavedCollection>> = _savedCollections.asStateFlow()

    private val _privacyPreferences = MutableStateFlow(
        PrivacyPreferences(
            allowMessages = true,
            allowMentions = true,
            showActivityStatus = true,
            sensitiveContentFilter = true,
            hiddenWords = listOf("spam", "spoiler", "إزعاج")
        )
    )
    val privacyPreferences: StateFlow<PrivacyPreferences> = _privacyPreferences.asStateFlow()

    private val _mediaLibrary = MutableStateFlow(loadPersistedMediaLibrary())
    val mediaLibrary: StateFlow<List<MediaAsset>> = _mediaLibrary.asStateFlow()

    private val _experiencePreferences = MutableStateFlow(ExperiencePreferences())
    val experiencePreferences: StateFlow<ExperiencePreferences> = _experiencePreferences.asStateFlow()

    private val _drafts = MutableStateFlow(
        listOf(
            PostDraft(
                id = "draft_1",
                text = "مسودة: كيف نصمم لحظات هادئة داخل المنتجات الاجتماعية بدون تقليل التفاعل؟",
                locationTag = "الرياض",
                updatedLabel = "منذ 12 دقيقة"
            )
        )
    )
    val drafts: StateFlow<List<PostDraft>> = _drafts.asStateFlow()

    private val _scheduledPosts = MutableStateFlow(
        listOf(
            ScheduledPost(
                id = "scheduled_1",
                text = "ملاحظات سريعة من جلسة اختبار واجهة المجالس الجديدة.",
                scheduledLabel = "اليوم • 8:45 م",
                locationTag = "الرياض"
            )
        )
    )
    val scheduledPosts: StateFlow<List<ScheduledPost>> = _scheduledPosts.asStateFlow()

    private val _recentSearches = MutableStateFlow(
        listOf("تصميم المنتجات", "Jetpack Compose", "الدرعية", "العمارة المستدامة")
    )
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _mutedUserIds = MutableStateFlow(setOf<String>())
    val mutedUserIds: StateFlow<Set<String>> = _mutedUserIds.asStateFlow()

    private val _blockedUserIds = MutableStateFlow(setOf<String>())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    // Clips with actual working video stream URLs
    private val _clips = MutableStateFlow(
        listOf(
            Clip(
                id = "clip_1",
                author = faisal,
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80",
                caption = "لحظات الغروب وتدرجات الضوء الطبيعي بين صخور وجبال العلا الخلابة. سحر المكان لا يوصف.",
                audioTrackTitle = "صوت الرياح والأصالة - العلا",
                likesCount = 18400,
                commentsCount = 920,
                isLiked = true
            ),
            Clip(
                id = "clip_2",
                author = _currentUser.value,
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?auto=format&fit=crop&w=600&q=80",
                caption = "نظرة سريعة على بساطة التفاعل وسلاسة الحركة في التصميم الجديد لمنصة سَمَر.",
                audioTrackTitle = "سَمَر • إيقاع الحوار الهادئ",
                likesCount = 29500,
                commentsCount = 1640,
                isLiked = false
            ),
            Clip(
                id = "clip_3",
                author = aziz,
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=600&q=80",
                caption = "استعراض كفاءة المعالجة السلسة بمعدل 120 إطاراً في الثانية على Jetpack Compose في أندرويد.",
                audioTrackTitle = "Electronic Waves - Modern Flow",
                likesCount = 14200,
                commentsCount = 680,
                isLiked = false
            )
        )
    )
    val clips: StateFlow<List<Clip>> = _clips.asStateFlow()

    private val _clipComments = MutableStateFlow(
        listOf(
            ClipComment(
                id = "clip_comment_1",
                clipId = "clip_2",
                author = aziz,
                text = "الانتقالات هنا نظيفة جدًا، خصوصًا حركة الطبقات.",
                timestampLabel = "9 د"
            ),
            ClipComment(
                id = "clip_comment_2",
                clipId = "clip_1",
                author = reem,
                text = "المشهد والضوء رائعان فعلًا.",
                timestampLabel = "21 د"
            )
        )
    )
    val clipComments: StateFlow<List<ClipComment>> = _clipComments.asStateFlow()

    // Direct Messages & Conversations
    private val _conversations = MutableStateFlow(
        listOf(
            Conversation(
                id = "conv_aziz",
                participant = aziz,
                lastMessage = "تم إرسال قياسات الأداء ومعدل استهلاك الذاكرة في شاشة الخلاصة.",
                lastTimestamp = "15:40",
                unreadCount = 2,
                isOnline = true
            ),
            Conversation(
                id = "conv_faisal",
                participant = faisal,
                lastMessage = "تسجيل صوتي (0:28)",
                lastTimestamp = "أمس",
                unreadCount = 0,
                isOnline = false
            ),
            Conversation(
                id = "conv_circle_design",
                participant = User(
                    id = "group_design",
                    username = "design.studio",
                    displayName = "استوديو التصميم",
                    avatarUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=500&q=80",
                    bio = "جلسات مراجعة واجهات المستخدم ونماذج التصميم"
                ),
                lastMessage = "ريم: اعتمدنا درجات الألوان الطبيعية للجناح المعماري الجديد.",
                lastTimestamp = "أمس",
                unreadCount = 0,
                isCircleChat = true,
                circleName = "استوديو التصميم"
            )
        )
    )
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Active Message Thread (with Aziz)
    private val _threadMessages = MutableStateFlow(
        listOf(
            DirectMessage(
                id = "msg_1",
                conversationId = "conv_aziz",
                senderId = "user_aziz",
                text = "مرحباً نورة، انتهينا من فحص أداء التمرير في خلاصة سَمَر الجديدة. النتائج ممتازة وبدون أي تجميد في الإطارات.",
                timestampFormatted = "15:30",
                isMine = false,
                status = MessageStatus.READ
            ),
            DirectMessage(
                id = "msg_2",
                conversationId = "conv_aziz",
                senderId = "user_noura",
                text = "أهلاً عبدالعزيز، هذا رائع جداً! هل اختبرتم استهلاك الذاكرة مع تشغيل مقاطع الفيديو في نفس الوقت؟",
                timestampFormatted = "15:32",
                isMine = true,
                status = MessageStatus.READ
            ),
            DirectMessage(
                id = "msg_3",
                conversationId = "conv_aziz",
                senderId = "user_aziz",
                text = "استمعي لملاحظات الاختبار السريع:",
                timestampFormatted = "15:35",
                isMine = false,
                voiceDurationSeconds = 24,
                voiceWaveform = listOf(0.2f, 0.5f, 0.9f, 0.8f, 0.6f, 0.4f, 0.7f, 1f, 0.6f, 0.3f, 0.7f, 0.4f),
                status = MessageStatus.READ
            ),
            DirectMessage(
                id = "msg_4",
                conversationId = "conv_aziz",
                senderId = "user_aziz",
                text = "تم إرسال قياسات الأداء ومعدل استهلاك الذاكرة في شاشة الخلاصة.",
                timestampFormatted = "15:40",
                isMine = false,
                status = MessageStatus.DELIVERED
            )
        )
    )
    val threadMessages: StateFlow<List<DirectMessage>> = _threadMessages.asStateFlow()

    // Communities (المجتمعات)
    private val _communities = MutableStateFlow(
        listOf(
            Community(
                id = "comm_arch",
                name = "معماريو الغد",
                description = "ملتقى رواد العمارة المستدامة، تقنيات الطين الحديثة، والتصميم البيئي في السعودية والعالم العربي.",
                avatarUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=500&q=80",
                coverUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1200&q=80",
                membersCount = 54200,
                isJoined = true,
                category = "العمارة والتصميم",
                rules = listOf("الحوار البناء وتبادل الخبرات", "احترام الملكية الفكرية للمشاريع")
            ),
            Community(
                id = "comm_tech",
                name = "مجتمع مطوري أندرويد",
                description = "مساحة تجمع مهندسي أندرويد لنقاشات Kotlin، Jetpack Compose، وأحدث معايير الأداء والأنظمة.",
                avatarUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=500&q=80",
                coverUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80",
                membersCount = 68400,
                isJoined = true,
                category = "التقنية والبرمجة"
            ),
            Community(
                id = "comm_photo",
                name = "مصورو الضوء والشارع",
                description = "ملتقى عشاق التصوير التناظري والتوثيقي، اقتناص لحظات الضوء الطبيعي وتفاصيل المدن.",
                avatarUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=500&q=80",
                coverUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=80",
                membersCount = 89100,
                isJoined = false,
                category = "الفنون البصرية"
            )
        )
    )
    val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    private val _majlisRooms = MutableStateFlow(
        listOf(
            MajlisRoom(
                id = "majlis_live_design",
                title = "كيف نصنع منتجًا عربيًا عالميًا؟",
                description = "جلسة مفتوحة عن بناء هوية رقمية عربية معاصرة، من الفكرة حتى التفاصيل الدقيقة في تجربة الاستخدام.",
                category = "التصميم والمنتج",
                host = _currentUser.value,
                coHosts = listOf(reem),
                status = MajlisStatus.LIVE,
                participantCount = 428,
                scheduledLabel = "مباشر الآن",
                accentHex = 0xFFD4AF37,
                isJoined = true
            ),
            MajlisRoom(
                id = "majlis_live_android",
                title = "Compose بلا تعقيد",
                description = "نقاش تقني عن القرارات التي تجعل واجهات Android أسرع وأسهل في الصيانة.",
                category = "التقنية",
                host = aziz,
                coHosts = listOf(faisal),
                status = MajlisStatus.LIVE,
                participantCount = 263,
                scheduledLabel = "مباشر الآن",
                accentHex = 0xFF38BDF8
            ),
            MajlisRoom(
                id = "majlis_upcoming_creative",
                title = "ضوء المدن: من الرياض إلى كيوتو",
                description = "جلسة بصرية بين التصوير والعمارة والهوية المكانية، مع مساحة أسئلة مفتوحة.",
                category = "الفنون البصرية",
                host = faisal,
                coHosts = listOf(elena),
                status = MajlisStatus.UPCOMING,
                participantCount = 1180,
                scheduledLabel = "اليوم • 9:00 م",
                accentHex = 0xFF8B5CF6
            ),
            MajlisRoom(
                id = "majlis_upcoming_career",
                title = "بناء حضور مهني بدون ضجيج",
                description = "كيف تستخدم الطبقات الاجتماعية والدوائر لبناء حضور مهني واضح دون خلط كل سياقات حياتك.",
                category = "المسار المهني",
                host = reem,
                coHosts = listOf(_currentUser.value),
                status = MajlisStatus.UPCOMING,
                participantCount = 742,
                scheduledLabel = "غدًا • 8:30 م",
                accentHex = 0xFF10B981
            )
        )
    )
    val majlisRooms: StateFlow<List<MajlisRoom>> = _majlisRooms.asStateFlow()

    // Smart Catch-up Summary
    val catchUpSummary = CatchUpSummary(
        missedPostsCount = 12,
        topDiscussions = _posts.value.take(2),
        circleUpdatesCount = 3,
        unreadMessagesCount = 2
    )

    // Mutations
    fun toggleLike(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newCount = if (newLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
                post.copy(isLiked = newLiked, likesCount = newCount)
            } else post
        }
    }

    fun toggleResonance(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val resonated = !post.isResonated
                post.copy(
                    isResonated = resonated,
                    resonanceCount = if (resonated) post.resonanceCount + 1 else maxOf(0, post.resonanceCount - 1)
                )
            } else post
        }
    }

    fun votePoll(postId: String, optionId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id != postId || post.poll == null || post.poll.isClosed) {
                post
            } else {
                val poll = post.poll
                val previous = poll.selectedOptionId
                val updatedOptions = poll.options.map { option ->
                    when {
                        option.id == previous && previous != optionId ->
                            option.copy(votes = maxOf(0, option.votes - 1))
                        option.id == optionId && previous != optionId ->
                            option.copy(votes = option.votes + 1)
                        else -> option
                    }
                }
                post.copy(
                    poll = poll.copy(
                        options = updatedOptions,
                        selectedOptionId = optionId
                    )
                )
            }
        }
    }

    fun toggleBookmark(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(isBookmarked = !post.isBookmarked) else post
        }
    }

    fun toggleRepost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(repostsCount = post.repostsCount + 1) else post
        }
    }

    fun toggleFollow(userId: String) {
        fun toggleUser(user: User): User {
            if (user.id != userId) return user
            val following = !user.isFollowing
            return user.copy(
                isFollowing = following,
                followersCount = if (following) user.followersCount + 1 else maxOf(0, user.followersCount - 1)
            )
        }

        _posts.value = _posts.value.map { post ->
            post.copy(
                author = toggleUser(post.author),
                collaborator = post.collaborator?.let(::toggleUser)
            )
        }
        _stories.value = _stories.value.map { story ->
            story.copy(author = toggleUser(story.author))
        }
        _echoNotes.value = _echoNotes.value.map { note ->
            note.copy(author = toggleUser(note.author))
        }
        _clips.value = _clips.value.map { clip ->
            clip.copy(author = toggleUser(clip.author))
        }
        _conversations.value = _conversations.value.map { conversation ->
            conversation.copy(participant = toggleUser(conversation.participant))
        }
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
        circle: SamrCircle?,
        lifetime: PostLifetime,
        collaborator: User?,
        allowComments: Boolean = true,
        hideLikeCount: Boolean = false,
        pollQuestion: String? = null,
        pollOptions: List<String> = emptyList(),
        locationTag: String? = null,
        altText: String? = null,
        mediaAssets: List<MediaAsset> = emptyList()
    ) {
        val newPost = Post(
            id = "post_${UUID.randomUUID().toString().take(8)}",
            author = _currentUser.value,
            text = text,
            mediaUrls = mediaUrls,
            mediaAssets = mediaAssets.take(10),
            timestampMinutesAgo = 1,
            likesCount = 0,
            commentsCount = 0,
            repostsCount = 0,
            isLiked = false,
            isBookmarked = false,
            circle = circle,
            socialLayer = _activeLayer.value,
            mood = _activeMood.value,
            lifetime = lifetime,
            collaborator = collaborator,
            allowComments = allowComments,
            hideLikeCount = hideLikeCount,
            locationTag = locationTag?.trim()?.takeIf { it.isNotBlank() },
            altText = altText?.trim()?.takeIf { it.isNotBlank() },
            allowDownloads = _privacyPreferences.value.allowMediaDownloads,
            allowRemix = _privacyPreferences.value.allowRemixes,
            poll = pollQuestion
                ?.trim()
                ?.takeIf { it.isNotBlank() && pollOptions.count { option -> option.isNotBlank() } >= 2 }
                ?.let { question ->
                    PostPoll(
                        question = question,
                        options = pollOptions
                            .filter { it.isNotBlank() }
                            .take(4)
                            .mapIndexed { index, option ->
                                PollOption(
                                    id = "poll_${UUID.randomUUID().toString().take(6)}_$index",
                                    text = option.trim()
                                )
                            }
                    )
                }
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    fun sendMessage(
        conversationId: String,
        text: String,
        voiceDuration: Int? = null,
        replyToText: String? = null
    ) {
        val newMsg = DirectMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = "user_noura",
            text = text,
            timestampFormatted = "الآن",
            isMine = true,
            voiceDurationSeconds = voiceDuration,
            voiceWaveform = if (voiceDuration != null) listOf(0.3f, 0.7f, 0.9f, 0.4f, 0.8f, 0.5f, 0.2f) else null,
            status = MessageStatus.SENT,
            replyToText = replyToText
        )
        _threadMessages.value = _threadMessages.value + newMsg
    }

    fun sendMediaMessage(
        conversationId: String,
        asset: MediaAsset,
        caption: String = "",
        replyToText: String? = null
    ) {
        val newMsg = DirectMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = _currentUser.value.id,
            text = caption.trim(),
            timestampFormatted = "الآن",
            isMine = true,
            mediaUrl = asset.uri,
            mediaAsset = asset,
            status = MessageStatus.SENT,
            replyToText = replyToText
        )
        _threadMessages.value = _threadMessages.value + newMsg
        _conversations.value = _conversations.value.map { conversation ->
            if (conversation.id == conversationId) {
                val label = when (asset.kind) {
                    MediaKind.IMAGE -> "صورة"
                    MediaKind.VIDEO -> "فيديو"
                    MediaKind.AUDIO -> "ملف صوتي"
                }
                conversation.copy(
                    lastMessage = caption.trim().ifBlank { label },
                    lastTimestamp = "الآن"
                )
            } else conversation
        }
    }

    fun toggleJoinCommunity(communityId: String) {
        _communities.value = _communities.value.map {
            if (it.id == communityId) it.copy(isJoined = !it.isJoined) else it
        }
    }

    fun toggleMajlisJoin(roomId: String) {
        _majlisRooms.value = _majlisRooms.value.map { room ->
            if (room.id != roomId) {
                room
            } else {
                val joined = !room.isJoined
                room.copy(
                    isJoined = joined,
                    participantCount = if (joined) room.participantCount + 1 else maxOf(0, room.participantCount - 1),
                    isHandRaised = if (joined) room.isHandRaised else false
                )
            }
        }
    }

    fun toggleMajlisHand(roomId: String) {
        _majlisRooms.value = _majlisRooms.value.map { room ->
            if (room.id == roomId && room.status == MajlisStatus.LIVE && room.isJoined) {
                room.copy(isHandRaised = !room.isHandRaised)
            } else room
        }
    }

    fun toggleMajlisReminder(roomId: String) {
        _majlisRooms.value = _majlisRooms.value.map { room ->
            if (room.id == roomId && room.status == MajlisStatus.UPCOMING) {
                room.copy(isReminderSet = !room.isReminderSet)
            } else room
        }
    }

    fun reactMajlis(roomId: String, reaction: String) {
        _majlisRooms.value = _majlisRooms.value.map { room ->
            if (room.id != roomId) room
            else when (reaction) {
                "applause" -> room.copy(applauseCount = room.applauseCount + 1)
                "heart" -> room.copy(heartCount = room.heartCount + 1)
                else -> room
            }
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

    fun toggleClipSave(clipId: String) {
        _clips.value = _clips.value.map { clip ->
            if (clip.id == clipId) clip.copy(isSaved = !clip.isSaved) else clip
        }
    }

    fun addClipComment(clipId: String, text: String) {
        if (text.isBlank()) return
        val comment = ClipComment(
            id = "clip_comment_${UUID.randomUUID().toString().take(8)}",
            clipId = clipId,
            author = _currentUser.value,
            text = text.trim()
        )
        _clipComments.value = listOf(comment) + _clipComments.value
        _clips.value = _clips.value.map { clip ->
            if (clip.id == clipId) clip.copy(commentsCount = clip.commentsCount + 1) else clip
        }
    }

    fun deleteClipComment(commentId: String) {
        val comment = _clipComments.value.firstOrNull { it.id == commentId } ?: return
        _clipComments.value = _clipComments.value.filterNot { it.id == commentId }
        _clips.value = _clips.value.map { clip ->
            if (clip.id == comment.clipId) clip.copy(commentsCount = maxOf(0, clip.commentsCount - 1)) else clip
        }
    }

    fun setEcho(text: String, emoji: String) {
        if (text.isBlank()) return
        val note = EchoNote(
            id = "echo_noura",
            author = _currentUser.value,
            text = text.trim(),
            emoji = emoji.trim().ifBlank { "✦" },
            timestampLabel = "الآن",
            isMine = true
        )
        _echoNotes.value = listOf(note) + _echoNotes.value.filterNot { it.isMine }
    }

    fun deleteMyEcho() {
        _echoNotes.value = _echoNotes.value.filterNot { it.isMine }
    }

    fun updateProfile(
        displayName: String,
        username: String,
        bio: String,
        location: String,
        avatarUrl: String
    ) {
        val updated = _currentUser.value.copy(
            displayName = displayName.trim(),
            username = username.trim().removePrefix("@"),
            bio = bio.trim(),
            location = location.trim(),
            avatarUrl = avatarUrl.trim().ifBlank { _currentUser.value.avatarUrl }
        )
        _currentUser.value = updated
        _posts.value = _posts.value.map { post ->
            if (post.author.id == updated.id) post.copy(author = updated) else post
        }
        _stories.value = _stories.value.map { story ->
            if (story.author.id == updated.id) story.copy(author = updated) else story
        }
        _clips.value = _clips.value.map { clip ->
            if (clip.author.id == updated.id) clip.copy(author = updated) else clip
        }
        _echoNotes.value = _echoNotes.value.map { note ->
            if (note.author.id == updated.id) note.copy(author = updated) else note
        }
        _comments.value = _comments.value.map { comment ->
            if (comment.author.id == updated.id) comment.copy(author = updated) else comment
        }
        _clipComments.value = _clipComments.value.map { comment ->
            if (comment.author.id == updated.id) comment.copy(author = updated) else comment
        }
    }

    fun editPost(postId: String, newText: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(text = newText.trim()) else post
        }
    }

    fun deletePost(postId: String) {
        _posts.value = _posts.value.filterNot { it.id == postId }
        _comments.value = _comments.value.filterNot { it.postId == postId }
        _savedCollections.value = _savedCollections.value.map { collection ->
            collection.copy(postIds = collection.postIds.filterNot { it == postId })
        }
    }

    fun togglePinPost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(isPinned = !post.isPinned) else post
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val post = _posts.value.firstOrNull { it.id == postId } ?: return
        if (!post.allowComments) return
        val newComment = PostComment(
            id = "comment_${UUID.randomUUID().toString().take(8)}",
            postId = postId,
            author = _currentUser.value,
            text = text.trim()
        )
        _comments.value = listOf(newComment) + _comments.value
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(commentsCount = it.commentsCount + 1) else it
        }
    }

    fun deleteComment(commentId: String) {
        val comment = _comments.value.firstOrNull { it.id == commentId } ?: return
        _comments.value = _comments.value.filterNot { it.id == commentId }
        _posts.value = _posts.value.map {
            if (it.id == comment.postId) it.copy(commentsCount = maxOf(0, it.commentsCount - 1)) else it
        }
    }

    fun toggleCommentLike(commentId: String) {
        _comments.value = _comments.value.map { comment ->
            if (comment.id == commentId) {
                val liked = !comment.isLiked
                comment.copy(
                    isLiked = liked,
                    likesCount = if (liked) comment.likesCount + 1 else maxOf(0, comment.likesCount - 1)
                )
            } else comment
        }
    }

    fun commentsForPost(postId: String): List<PostComment> =
        _comments.value.filter { it.postId == postId }

    fun addStory(mediaUrl: String, caption: String) {
        val story = Story(
            id = "story_${UUID.randomUUID().toString().take(8)}",
            author = _currentUser.value,
            mediaUrl = mediaUrl.trim().ifBlank {
                "https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=900&q=80"
            },
            timestampMinutesAgo = 0,
            caption = caption.trim()
        )
        _stories.value = listOf(story) + _stories.value
    }

    fun deleteStory(storyId: String) {
        _stories.value = _stories.value.filterNot { it.id == storyId }
    }

    fun publishAssetAsStory(asset: MediaAsset, caption: String = "") {
        if (asset.kind == MediaKind.AUDIO) return
        val story = Story(
            id = "story_${UUID.randomUUID().toString().take(8)}",
            author = _currentUser.value,
            mediaUrl = asset.uri,
            mediaKind = asset.kind,
            timestampMinutesAgo = 0,
            caption = caption.trim().ifBlank { asset.title }
        )
        _stories.value = listOf(story) + _stories.value
    }

    fun markStoryViewed(storyId: String) {
        _stories.value = _stories.value.map { story ->
            if (story.id == storyId) story.copy(isViewed = true) else story
        }
    }

    fun createCircle(nameAr: String, nameEn: String, description: String) {
        if (nameAr.isBlank() && nameEn.isBlank()) return
        val circle = SamrCircle(
            id = "circle_${UUID.randomUUID().toString().take(8)}",
            nameEn = nameEn.trim().ifBlank { nameAr.trim() },
            nameAr = nameAr.trim().ifBlank { nameEn.trim() },
            description = description.trim(),
            colorHex = 0xFFD4AF37,
            memberCount = 1
        )
        _circles.value = _circles.value + circle
    }

    fun deleteCircle(circleId: String) {
        _circles.value = _circles.value.filterNot { it.id == circleId }
        _posts.value = _posts.value.map { post ->
            if (post.circle?.id == circleId) post.copy(circle = null) else post
        }
    }

    fun markNotificationRead(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun deleteNotification(notificationId: String) {
        _notifications.value = _notifications.value.filterNot { it.id == notificationId }
    }

    fun updatePrivacyPreferences(
        allowMessages: Boolean,
        allowMentions: Boolean,
        showActivityStatus: Boolean,
        sensitiveContentFilter: Boolean,
        hiddenWords: List<String>,
        allowMediaDownloads: Boolean = _privacyPreferences.value.allowMediaDownloads,
        allowRemixes: Boolean = _privacyPreferences.value.allowRemixes,
        allowClipReuse: Boolean = _privacyPreferences.value.allowClipReuse
    ) {
        _privacyPreferences.value = PrivacyPreferences(
            allowMessages = allowMessages,
            allowMentions = allowMentions,
            showActivityStatus = showActivityStatus,
            sensitiveContentFilter = sensitiveContentFilter,
            allowMediaDownloads = allowMediaDownloads,
            allowRemixes = allowRemixes,
            allowClipReuse = allowClipReuse,
            hiddenWords = hiddenWords
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .take(30)
        )
    }

    fun updateExperiencePreferences(
        autoplayVideos: Boolean,
        reducedMotion: Boolean,
        compactFeed: Boolean,
        hapticFeedback: Boolean,
        highQualityMedia: Boolean,
        showReadReceipts: Boolean
    ) {
        _experiencePreferences.value = ExperiencePreferences(
            autoplayVideos = autoplayVideos,
            reducedMotion = reducedMotion,
            compactFeed = compactFeed,
            hapticFeedback = hapticFeedback,
            highQualityMedia = highQualityMedia,
            showReadReceipts = showReadReceipts
        )
    }

    fun saveDraft(
        text: String,
        mediaUrl: String?,
        locationTag: String,
        altText: String,
        mediaAssets: List<MediaAsset> = emptyList()
    ) {
        if (text.isBlank() && mediaUrl.isNullOrBlank() && mediaAssets.isEmpty()) return
        val draft = PostDraft(
            id = "draft_${UUID.randomUUID().toString().take(8)}",
            text = text.trim(),
            mediaUrl = mediaUrl?.trim()?.takeIf { it.isNotBlank() },
            mediaAssets = mediaAssets.take(10),
            locationTag = locationTag.trim(),
            altText = altText.trim(),
            updatedLabel = "الآن"
        )
        _drafts.value = listOf(draft) + _drafts.value
    }

    fun deleteDraft(draftId: String) {
        _drafts.value = _drafts.value.filterNot { it.id == draftId }
    }

    fun schedulePost(
        text: String,
        mediaUrl: String?,
        scheduledLabel: String,
        locationTag: String,
        altText: String,
        mediaAssets: List<MediaAsset> = emptyList()
    ) {
        if (text.isBlank() && mediaUrl.isNullOrBlank() && mediaAssets.isEmpty()) return
        val scheduled = ScheduledPost(
            id = "scheduled_${UUID.randomUUID().toString().take(8)}",
            text = text.trim(),
            mediaUrl = mediaUrl?.trim()?.takeIf { it.isNotBlank() },
            mediaAssets = mediaAssets.take(10),
            scheduledLabel = scheduledLabel.trim().ifBlank { "لاحقًا" },
            locationTag = locationTag.trim(),
            altText = altText.trim()
        )
        _scheduledPosts.value = listOf(scheduled) + _scheduledPosts.value
    }

    fun deleteScheduledPost(postId: String) {
        _scheduledPosts.value = _scheduledPosts.value.filterNot { it.id == postId }
    }

    fun publishScheduledPost(postId: String) {
        val scheduled = _scheduledPosts.value.firstOrNull { it.id == postId } ?: return
        publishPost(
            text = scheduled.text,
            mediaUrls = scheduled.mediaUrl?.let(::listOf) ?: emptyList(),
            circle = null,
            lifetime = PostLifetime.PERMANENT,
            collaborator = null,
            locationTag = scheduled.locationTag,
            altText = scheduled.altText,
            mediaAssets = scheduled.mediaAssets
        )
        deleteScheduledPost(postId)
    }

    private fun loadPersistedMediaLibrary(): List<MediaAsset> {
        val raw = mediaPrefs?.getString("assets", null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        MediaAsset(
                            id = item.getString("id"),
                            uri = item.getString("uri"),
                            kind = MediaKind.valueOf(item.getString("kind")),
                            origin = MediaOrigin.valueOf(item.getString("origin")),
                            title = item.optString("title"),
                            mimeType = item.optString("mimeType"),
                            durationMs = item.optLong("durationMs", 0L),
                            trimStartMs = item.optLong("trimStartMs", 0L),
                            trimEndMs = if (item.has("trimEndMs") && !item.isNull("trimEndMs")) item.getLong("trimEndMs") else null,
                            playbackSpeed = item.optDouble("playbackSpeed", 1.0).toFloat(),
                            isMuted = item.optBoolean("isMuted", false),
                            filterName = item.optString("filterName", "Original"),
                            overlayText = item.optString("overlayText"),
                            altText = item.optString("altText"),
                            isFavorite = item.optBoolean("isFavorite", false)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun persistMediaLibrary() {
        val prefs = mediaPrefs ?: return
        val array = JSONArray()
        _mediaLibrary.value.forEach { asset ->
            val item = JSONObject()
                .put("id", asset.id)
                .put("uri", asset.uri)
                .put("kind", asset.kind.name)
                .put("origin", asset.origin.name)
                .put("title", asset.title)
                .put("mimeType", asset.mimeType)
                .put("durationMs", asset.durationMs)
                .put("trimStartMs", asset.trimStartMs)
                .put("playbackSpeed", asset.playbackSpeed.toDouble())
                .put("isMuted", asset.isMuted)
                .put("filterName", asset.filterName)
                .put("overlayText", asset.overlayText)
                .put("altText", asset.altText)
                .put("isFavorite", asset.isFavorite)
            if (asset.trimEndMs != null) item.put("trimEndMs", asset.trimEndMs)
            array.put(item)
        }
        prefs.edit().putString("assets", array.toString()).apply()
    }

    fun remixPostToLibrary(postId: String): List<MediaAsset> {
        val post = _posts.value.firstOrNull { it.id == postId } ?: return emptyList()
        if (!post.allowRemix) return emptyList()

        val assets = if (post.mediaAssets.isNotEmpty()) {
            post.mediaAssets.map { asset ->
                asset.copy(
                    id = "media_${UUID.randomUUID().toString().take(8)}",
                    origin = MediaOrigin.STUDIO,
                    title = "Remix • " + asset.title
                )
            }
        } else {
            post.mediaUrls.mapIndexed { index, url ->
                MediaAsset(
                    id = "media_${UUID.randomUUID().toString().take(8)}_$index",
                    uri = url,
                    kind = MediaKind.IMAGE,
                    origin = MediaOrigin.IMPORTED,
                    title = "Remix • " + post.author.displayName,
                    mimeType = "image/jpeg",
                    altText = post.altText.orEmpty()
                )
            }
        }

        addMediaAssets(assets)
        return assets
    }

    fun addMediaAsset(asset: MediaAsset) {
        _mediaLibrary.value = listOf(asset) + _mediaLibrary.value.filterNot { it.id == asset.id }
        persistMediaLibrary()
    }

    fun addMediaAssets(assets: List<MediaAsset>) {
        assets.reversed().forEach(::addMediaAsset)
    }

    fun updateMediaAsset(asset: MediaAsset) {
        _mediaLibrary.value = _mediaLibrary.value.map {
            if (it.id == asset.id) asset else it
        }
        persistMediaLibrary()
    }

    fun deleteMediaAsset(assetId: String) {
        _mediaLibrary.value = _mediaLibrary.value.filterNot { it.id == assetId }
        persistMediaLibrary()
    }

    fun duplicateMediaAsset(assetId: String) {
        val source = _mediaLibrary.value.firstOrNull { it.id == assetId } ?: return
        addMediaAsset(
            source.copy(
                id = "media_${UUID.randomUUID().toString().take(8)}",
                title = source.title + " • نسخة"
            )
        )
    }

    fun toggleMediaFavorite(assetId: String) {
        _mediaLibrary.value = _mediaLibrary.value.map {
            if (it.id == assetId) it.copy(isFavorite = !it.isFavorite) else it
        }
        persistMediaLibrary()
    }

    fun publishVideoAssetAsClip(asset: MediaAsset, caption: String) {
        if (asset.kind != MediaKind.VIDEO) return
        val clip = Clip(
            id = "clip_${UUID.randomUUID().toString().take(8)}",
            author = _currentUser.value,
            videoUrl = asset.uri,
            thumbnailUrl = "",
            caption = caption.trim().ifBlank { asset.title },
            audioTrackTitle = if (asset.isMuted) "بدون صوت" else "الصوت الأصلي",
            likesCount = 0,
            commentsCount = 0
        )
        _clips.value = listOf(clip) + _clips.value
    }

    fun addRecentSearch(query: String) {
        val normalized = query.trim()
        if (normalized.isBlank()) return
        _recentSearches.value = listOf(normalized) +
            _recentSearches.value.filterNot { it.equals(normalized, ignoreCase = true) }.take(7)
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun toggleMuteUser(userId: String) {
        _mutedUserIds.value = if (userId in _mutedUserIds.value) {
            _mutedUserIds.value - userId
        } else {
            _mutedUserIds.value + userId
        }
    }

    fun toggleBlockUser(userId: String) {
        _blockedUserIds.value = if (userId in _blockedUserIds.value) {
            _blockedUserIds.value - userId
        } else {
            _blockedUserIds.value + userId
        }
        if (userId in _blockedUserIds.value) {
            _mutedUserIds.value = _mutedUserIds.value + userId
        }
    }

    fun reportPost(postId: String) {
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(isReported = true) else it
        }
    }

    fun hidePost(postId: String) {
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(isHidden = true) else it
        }
    }

    fun createSavedCollection(title: String) {
        if (title.isBlank()) return
        _savedCollections.value = _savedCollections.value + SavedCollection(
            id = "collection_${UUID.randomUUID().toString().take(8)}",
            title = title.trim()
        )
    }

    fun deleteSavedCollection(collectionId: String) {
        _savedCollections.value = _savedCollections.value.filterNot { it.id == collectionId }
    }

    fun togglePostInCollection(collectionId: String, postId: String) {
        _savedCollections.value = _savedCollections.value.map { collection ->
            if (collection.id != collectionId) {
                collection
            } else {
                val ids = if (postId in collection.postIds) {
                    collection.postIds - postId
                } else {
                    collection.postIds + postId
                }
                collection.copy(postIds = ids)
            }
        }
    }

    fun editMessage(messageId: String, text: String) {
        if (text.isBlank()) return
        _threadMessages.value = _threadMessages.value.map {
            if (it.id == messageId && it.isMine) it.copy(text = text.trim()) else it
        }
    }

    fun reactToMessage(messageId: String, reaction: String?) {
        _threadMessages.value = _threadMessages.value.map { message ->
            if (message.id == messageId) message.copy(reaction = reaction) else message
        }
    }

    fun toggleConversationPin(conversationId: String) {
        _conversations.value = _conversations.value.map { conversation ->
            if (conversation.id == conversationId) conversation.copy(isPinned = !conversation.isPinned) else conversation
        }
    }

    fun toggleConversationMute(conversationId: String) {
        _conversations.value = _conversations.value.map { conversation ->
            if (conversation.id == conversationId) conversation.copy(isMuted = !conversation.isMuted) else conversation
        }
    }

    fun markConversationRead(conversationId: String) {
        _conversations.value = _conversations.value.map { conversation ->
            if (conversation.id == conversationId) conversation.copy(unreadCount = 0) else conversation
        }
        _threadMessages.value = _threadMessages.value.map { message ->
            if (message.conversationId == conversationId && !message.isMine) {
                message.copy(status = MessageStatus.READ)
            } else message
        }
    }

    fun deleteMessage(messageId: String) {
        _threadMessages.value = _threadMessages.value.filterNot { it.id == messageId && it.isMine }
    }

    fun deleteConversation(conversationId: String) {
        _conversations.value = _conversations.value.filterNot { it.id == conversationId }
        _threadMessages.value = _threadMessages.value.filterNot { it.conversationId == conversationId }
    }
}
