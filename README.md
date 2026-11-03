# زنجیر (Zanjir)

پیام‌رسان مستقل اندروید با رابط کاربری بومی Jetpack Compose و هستهٔ ارتباطی Gereh.

## وضعیت توسعه

- رابط مستقل زنجیر و آیکون اختصاصی در این مخزن قرار دارند.
- هستهٔ ارتباطی از ماژول کتابخانه‌ای Gereh استفاده می‌کند؛ رابط تلگرام و اپ میزبان MLMVPN وارد پروژه نشده‌اند.
- APK عمداً در این مرحله ساخته نمی‌شود.
- تا زمان ساخت کتابخانهٔ بومی `libmlmchat.so` و اجرای آزمون روی دو دستگاه، پیام‌رسانی واقعی را آماده/تأییدشده تلقی نکنید.

## دریافت کد هسته

این پروژه از Gereh به‌عنوان زیرماژول Git و در یک commit مشخص استفاده می‌کند. پس از clone:

```bash
git submodule update --init --recursive
```

سپس کتابخانهٔ بومی را با Android NDK r27+ و Rust بسازید:

```bash
cd vendor/gereh/mlm-chat/android
cargo install cargo-ndk
rustup target add aarch64-linux-android armv7-linux-androideabi
ANDROID_NDK_HOME=/path/to/android-ndk bash scripts/build-native.sh
```

کتابخانهٔ Kotlin در Gradle با نام `:gereh-core` اضافه شده است.

## مجوزها

کد هستهٔ Gereh تحت Apache-2.0 است. فایل‌های مجوز و اعلان‌های بالادستی باید همراه توزیع حفظ شوند. این پروژه رابط Telegram/GPL را کپی نمی‌کند.
