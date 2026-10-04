# سَمَر — SAMR

تطبيق تواصل اجتماعي Android Native موجه أولاً للسوق السعودي والعربي، مع دعم كامل للإنجليزية وتجربة عالمية قابلة للتوسع.

## الحالة الحالية

### SAMR 0.7.0 — Creator & Media Studio
- استوديو وسائط كامل داخل التطبيق لإنشاء واستيراد وإدارة الصور والفيديو والصوت
- CameraX داخل SAMR لالتقاط الصور وتسجيل الفيديو مع تبديل الكاميرا والفلاش
- تسجيل صوت AAC فعلي مع Timer وPause/Resume وحفظ التسجيل في مكتبة دائمة
- مولّد تصميمات داخل التطبيق ينتج صور JPEG فعلية بمقاسات 1:1 و9:16 و16:9
- محرر صور فعلي: Warm/Cool/Noir/Vibrant + Text Overlay وإخراج نسخة جديدة
- مكتبة وسائط مستمرة بعد إعادة تشغيل التطبيق: بحث، فلاتر، مفضلة، نسخ، حذف وتحديد متعدد
- منشورات Mixed Media تعرض صورة/فيديو/صوت داخل Feed مع Media3
- نشر الفيديو مباشرة كـClip ونشر الصورة أو الفيديو مباشرة كـStory
- إرسال صورة/فيديو/صوت فعلي داخل الرسائل مع Preview وCaption وReply
- Composer يدعم حتى 10 وسائط وإعادة ترتيبها وحذفها قبل النشر
- Drafts وScheduled Posts يحتفظان بقائمة الوسائط
- تنزيل الوسائط المحلية أو البعيدة مع احترام صلاحية صاحب المحتوى
- Remix للمنشورات إلى مكتبة SAMR Studio مع صلاحيات تحكم
- Privacy Center يدير تنزيل الوسائط وRemix وإعادة استخدام Clips
- ملفات الاستوديو تُحفظ في مساحة داخلية دائمة وآمنة عبر FileProvider
- إعداد Autoplay من Experience Center يتحكم في الفيديو داخل المنشورات
- اختبارات إضافية لمكتبة الوسائط والمنشورات والقصص والرسائل

ملاحظة تقنية: قص الفيديو والسرعة والكتم في هذه المرحلة تعديلات غير تدميرية تُطبّق أثناء المعاينة والتشغيل، وليست بعد عملية Transcoding/Export لملف MP4 جديد. البنية الاجتماعية السحابية متعددة المستخدمين ما زالت مرحلة منفصلة عن هذا الـPreview.

### SAMR 0.6.0 — 50+ Product & UX Upgrades
- Content Hub للمسودات والمنشورات المجدولة والاستكمال والنشر الفوري
- Composer احترافي: جدولة، Location، Alt Text، عداد أحرف، Draft workflow
- Experience Center: Autoplay، Reduced Motion، Compact Feed، Haptics، Media Quality، Read Receipts
- Post moderation: Hide، Report، Mute Creator، metadata وAccessibility
- Feed ذكي يحترم المحتوى المخفي والحسابات المكتومة وكثافة العرض
- Comment likes وStory viewed states وSocial Pulse
- Discover: Recent Searches، Clear History، People/Posts/Communities filters
- Profile: Links قابلة للفتح، Achievements، Content Hub
- Messaging: Pin/Mute conversations، Unread filter، conversation search، thread search، replies، reactions، read handling
- Activity Center: All/Unread/Social/System filters
- Majlis: live applause and heart reactions مع counters
- عشرات تحسينات الحالات البصرية، التفاعل، الـmicro UX، وإدارة المحتوى

### SAMR 0.5.0
- Polls تفاعلية داخل المنشورات مع التصويت والنتائج الحية
- مجالس سَمَر: غرف حوار مباشر وفعاليات قادمة وتصنيفات وهوية عربية حديثة
- دخول ومغادرة المجالس ورفع اليد وتذكيرات الجلسات القادمة
- Privacy & Safety Center فعلي لإدارة الرسائل والإشارات وحالة النشاط وفلتر المحتوى والكلمات المخفية
- توسعة تجربة Home للوصول السريع إلى المجالس
- تحسينات إضافية في البنية والتفاعل والهوية البصرية

### SAMR 0.4.0
- Echo Notes قصيرة ومؤقتة فوق القصص
- Resonance كتفاعل اجتماعي جديد مستقل عن الإعجاب
- Creator Studio بقراءات الأداء والوصول والتفاعل وإشارات الجمهور
- موجز ذكي وإجراءات سريعة في الصفحة الرئيسية
- Clips بتعليقات وحفظ ومشاركة ومتابعة فعلية
- بحث مباشر في الأشخاص والمجتمعات والمنشورات
- تحسينات إضافية في التسلسل البصري والتنقل والتفاعل

### SAMR 0.3.0
- مركز نشاط للإشعارات والمجموعات والدوائر
- قصص وتعليقات ومنشورات قابلة للإضافة والتعديل والحذف
- بحث فعلي في الأشخاص والمجتمعات
- مشاركة Android أصلية
- إدارة رسائل ومحادثات محسنة
- ملف شخصي قابل للتعديل مع تبويب مقاطع فعلي
- تحكم متقدم في الجمهور والتعليقات وإظهار الإعجابات

### منفذ ويعمل داخل نسخة العرض
- Kotlin + Jetpack Compose
- هوية SAMR / سَمَر
- واجهات عربية RTL وإنجليزية LTR
- اختيار لغة داخل التطبيق
- Light / Dark / OLED
- Home Feed وStories وSocial Layers وQuiet Feed
- Discover وCommunities
- Create Post
- Clips باستخدام Media3 / ExoPlayer
- Messaging demo
- Profile وHighlights
- Demo session محلية واضحة وغير مرتبطة بمصادقة إنتاجية

### يعمل ببيانات تجريبية
المحتوى والحسابات والمحادثات تأتي حالياً من SamrRepository المحلي حتى تظل النسخة قابلة للتجربة بدون خادم مدفوع.

### المرحلة التالية
المصادقة السحابية، قاعدة البيانات، التخزين، الإشعارات، والمزامنة الحقيقية ستُربط في مرحلة Backend منفصلة.

## Android
- Application ID: com.samr.social
- Minimum SDK: 24
- Target SDK: 36
- Preview version: 0.7.0

## CI

حالة البناء: GitHub Actions preview pipeline enabled.
GitHub Actions يبني الاختبارات وAPK التجريبي تلقائياً على كل تحديث للفرع main.

---

# SAMR

SAMR is a Saudi-first, Arabic-native Android social network showcase built with Kotlin and Jetpack Compose. The current build is an interactive demo-backed preview; production cloud authentication and remote persistence are intentionally deferred to the backend phase.
