package com.samr.social.core.repository

import com.samr.social.core.model.CatchUpSummary
import com.samr.social.core.model.Clip
import com.samr.social.core.model.Community
import com.samr.social.core.model.Conversation
import com.samr.social.core.model.DirectMessage
import com.samr.social.core.model.MessageStatus
import com.samr.social.core.model.MoodType
import com.samr.social.core.model.Post
import com.samr.social.core.model.PostLifetime
import com.samr.social.core.model.SamrCircle
import com.samr.social.core.model.SocialLayer
import com.samr.social.core.model.Story
import com.samr.social.core.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SamrRepository {

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
            activeSocialLayer = SocialLayer.PERSONAL
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
                discussionRoomId = "room_ux_simplicity"
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
                discussionRoomId = "room_android_architecture"
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
        // Toggle follow in memory
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
        collaborator: User?
    ) {
        val newPost = Post(
            id = "post_${UUID.randomUUID().toString().take(8)}",
            author = _currentUser.value,
            text = text,
            mediaUrls = mediaUrls,
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
            collaborator = collaborator
        )
        _posts.value = listOf(newPost) + _posts.value
    }

    fun sendMessage(conversationId: String, text: String, voiceDuration: Int? = null) {
        val newMsg = DirectMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = "user_noura",
            text = text,
            timestampFormatted = "الآن",
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
