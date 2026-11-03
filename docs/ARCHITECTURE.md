# معماری زنجیر

- **UI:** Jetpack Compose در `app/`؛ بدون Telegram UI.
- **Core:** ماژول Kotlin/Rust Gereh به‌عنوان Git submodule در `vendor/gereh`؛ فقط کتابخانهٔ `mlm-chat/android/core` به Gradle معرفی می‌شود.
- **Native:** `mlm-chat/android/jni` و `mlm-chat/core` برای تولید `libmlmchat.so`.
- **Identity & storage:** API هسته، با پایگاه دادهٔ رمزگذاری‌شده و کلید نگه‌داری‌شده در Android Keystore.
- **Transport:** ابتدا LAN/direct؛ P2P/relay فقط بعد از تنظیم و آزمون. داوطلب relay به‌صورت پیش‌فرض خاموش می‌ماند.
- **UI independence:** هیچ ماژول UI تلگرام یا اپ MLMVPN به Gradle اضافه نمی‌شود.

## محدودیت فعلی

تا وقتی کتابخانهٔ بومی برای ABIهای موردنیاز ساخته و در دستگاه واقعی آزمایش نشده، هیچ عملیات هسته‌ای را نمی‌توان عملیاتی اعلام کرد. همچنین باید قبل از انتشار، آدرس‌ها و کلیدهای رلهٔ اختصاصی زنجیر تعریف شوند؛ از مقادیر placeholder بالادستی استفاده نشود.
