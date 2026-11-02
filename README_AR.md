# مشروع VOYA للموبايل — Android وiOS

**الحالة:** جاهز لإجراء البناء عبر أدوات Android وXcode؛ لم تُبنَ حزمة APK أو IPA من هذه البيئة، ولم تُختبر نسخة المتجر.

## كيف تحصل على نسخة Android APK تجريبية؟
1. أنشئ مستودع GitHub مستقل باسم VOYA-Mobile، وارفـع إليه **محتويات** الأرشيف (مو ملف ZIP نفسه)، مع إظهار مجلد `.github` المخفي.
2. افتح تبويب **Actions** في المستودع، واختر **VOYA Mobile Development Builds** ثم **Run workflow**. أيضاً يمكن أن يبدأ بعد أول رفع إلى `main`.
3. بعد ظهور نجاح وظيفة `android-debug`، افتح التشغيل وحمّل ملف **VOYA-Android-Debug-APK** من Artifacts.
4. الملف بداخله `app-debug.apk` للتجارب وليس إصدار Play Store النهائي.

الطريقة المحلية: Android Studio مع JDK17، Gradle 8.13، Android SDK API36؛ افتح `Android/` ثم Build APK أو شغّل `scripts/build_android_debug.sh` من جذر المشروع.

## بناء iOS وTestFlight
1. يحتاج **Mac مع Xcode 26+** وحساب Apple Developer فعّال وإعداد شهادات التوقيع.
2. افتح `iOS/VOYA.xcodeproj` وحدد Team في Signing & Capabilities.
3. للتجربة على المحاكي: شغّل `scripts/build_ios_simulator.sh` على Mac. وظيفة GitHub Actions ترفع ملف `.app.zip` **خاص بالمحاكي فقط**.
4. لجهاز iPhone وتوزيع TestFlight: اضبط `APPLE_DEVELOPMENT_TEAM` على جهاز Mac، ثم شغّل `scripts/build_ios_archive.sh`. بعد نجاح الأرشفة، من Xcode Organizer استخدم Distribute App → App Store Connect → Upload.
5. لا ترسل أي شهادة أو مفتاح أو كلمة مرور في المحادثة أو إلى GitHub كمحتوى ملف؛ اعتمد أسرار النظام المخصصة.

## ما الذي لا يعمل تلقائياً؟
النسختان تغلفان موقع VOYA في WebView؛ لا تتضمنان مصدر VOYA الأصلي أو خدمات حقيقية للبث والمراسلة والمصادقة والمشتريات. تسجيل الدخول عبر Apple/Facebook داخل WebView يحتاج تكاملات أصلية وإصلاحات للموقع. لا يجوز اعتبار أي خدمة تجريبية ميزة صالحة للمتجر قبل الاختبارات.

## الاختبارات التي أجريت هنا
- `scripts/verify_source.py` تدقيق بنيوي للكود وملفات XML/Plist.
- فحص Swift الصياغي فقط، وليس ربطه بأطر iOS أو بناء تطبيق.
- فحص صيغة ملف Xcode `project.pbxproj`.
- تعذر تنزيل Android SDK/Gradle من هذه البيئة، ولا يتوفر Xcode للآيفون.

**الموقع المعتمد:** https://voya-app.funpony.chatgpt.site/

**الهوية:** VOYA / LIVE TRAVEL EXPLORATION — Powered by ASAS 360°.
