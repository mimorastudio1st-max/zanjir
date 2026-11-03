# بررسی upstream Gereh

- مخزن مرجع: https://github.com/mlmvpn/mlmvpn_android
- هسته Rust: `mlm-chat/core`
- JNI و API کاتلین: `mlm-chat/android`
- بستهٔ Kotlin: `org.mlmchat.core`
- ماژول اندروید: `mlm-chat/android/core`
- رابط Telegram-derived در `chat/` است و نباید کورکورانه به اپ مستقل منتقل شود.

مستند upstream نام‌هایی مانند `PendingIdentity`, `ChatNode.open`, `ChatNode.startDirect`, `ChatNode.addContact`, `ChatNode.sendText`, `ChatNode.history` و `ChatNode.pollEvent` را ذکر می‌کند. امضاها باید در revision مشخص upstream تأیید شوند.

ساخت کتابخانهٔ native طبق دستورالعمل upstream به Android NDK r27+، Rust targets و `cargo-ndk` نیاز دارد. هنگام انتقال کد، فایل‌های LICENSE و copyright همان اجزا حفظ شوند؛ هسته و UI مشتق‌شده مجوزهای متفاوت دارند.
