# VOYA Mobile — GitHub setup & build handoff

تاريخ التحقق: 1 نوفمبر 2026

## الإجراء المطلوب من مالك حساب GitHub

1. افتح https://github.com/new وسجّل دخولك إلى حساب GitHub الخاص بك.
2. Owner: `ahmedwaleed07`، وRepository name: `VOYA-Mobile`، واختر **Private**.
3. أنشئ المستودع مع تفعيل خيار **Add a README file**، حتى يتوفر فرع `main` من أول لحظة.
4. تأكد من أن GitHub المرتبط بـChatGPT مخوّل بالوصول إلى المستودع الجديد؛ قد يحتاج إضافة المستودع إلى قائمة المستودعات المسموح بها في إعدادات تثبيت التطبيق.
5. شارك رابط المستودع: `https://github.com/ahmedwaleed07/VOYA-Mobile` هنا حتى نرفع المصدر ونراجع التشغيل.

## ما هو جاهز بهذا الأرشيف؟

- `Android/` — Android Java + WebView، `com.voya.app`، Target API 36.
- `iOS/` — SwiftUI + WKWebView project مع الأذونات اللازمة.
- `.github/workflows/mobile-build.yml` — GitHub Actions لبناء Android Debug APK وiOS Simulator App.
- `scripts/verify_source.py` — فحوصات بنيوية سريعة.
- `README_AR.md` و`RELEASE-CHECKLIST.md` — الوثائق والتعليمات.

## إن أردت رفع المصدر من حاسوبك بنفسك

بعد فك ضغط الحزمة في مجلد مستقل، نفّذ الأوامر الآتية (بعد إنشاء المستودع وتثبيت Git):

```bash
git init
git branch -M main
git add .
git commit -m "Initial VOYA Android and iOS CI preview"
git remote add origin https://github.com/ahmedwaleed07/VOYA-Mobile.git
git push -u origin main
```

إذا أنشأت المستودع مع README أولاً، فالرفع اليدوي بهذه الأوامر يتطلب سحب التاريخ الموجود أو استخدام مستودع فارغ بدون README؛ تجنب `--force`. الأسهل ترك رفع الملفات للمساعد بعد إتاحة المستودع المرتبط.

## بعد رفع الملفات

يفترض أن يبدأ GitHub Actions عند الدفع إلى فرع `main`. راجع Actions > VOYA Mobile Development Builds. سيتم نشر Android Debug APK ونسخة **iOS Simulator** في Artifacts إذا اكتمل البناء دون أخطاء. نسخة iOS Simulator ليست IPA قابلة للتثبيت على iPhone ولا نسخة TestFlight. رفع iOS يتطلب شهادة توقيع وفريق Apple Developer، واختبارات حقيقية.

**تنبيه:** هذه نسخ WebView تجريبية تعتمد على رابط VOYA الحالي. لم تُبنَ APK/IPA فعلياً في بيئة العمل المحلية (لا Android SDK ولا Xcode) ولم يتم التحقق من الخدمات الخلفية والبث والمراسلات الحقيقية. غير جاهزة للنشر العام قبل الاختبارات والامتثال.
