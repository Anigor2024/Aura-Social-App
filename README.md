# سَمَر — SAMR

تطبيق تواصل اجتماعي Android Native موجه أولاً للسوق السعودي والعربي، مع دعم كامل للإنجليزية وتجربة عالمية قابلة للتوسع.

## الحالة الحالية

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
- Preview version: 0.3.0

## CI

حالة البناء: GitHub Actions preview pipeline enabled.
GitHub Actions يبني الاختبارات وAPK التجريبي تلقائياً على كل تحديث للفرع main.

---

# SAMR

SAMR is a Saudi-first, Arabic-native Android social network showcase built with Kotlin and Jetpack Compose. The current build is an interactive demo-backed preview; production cloud authentication and remote persistence are intentionally deferred to the backend phase.
