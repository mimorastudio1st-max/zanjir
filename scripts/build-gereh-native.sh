#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
git submodule update --init --recursive vendor/gereh
: "${ANDROID_NDK_HOME:?Set ANDROID_NDK_HOME to Android NDK r27 or newer}"
command -v cargo >/dev/null || { echo "Rust/Cargo is required" >&2; exit 1; }
command -v cargo-ndk >/dev/null || { echo "Install cargo-ndk: cargo install cargo-ndk" >&2; exit 1; }
rustup target add aarch64-linux-android armv7-linux-androideabi
cd "$ROOT/vendor/gereh/mlm-chat/android"
bash scripts/build-native.sh arm64-v8a armeabi-v7a
