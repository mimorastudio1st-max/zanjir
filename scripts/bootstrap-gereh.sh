#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
git submodule update --init --recursive vendor/gereh
test -f vendor/gereh/mlm-chat/android/core/src/main/java/org/mlmchat/core/ChatNode.kt
echo "Gereh Android core source is present."
echo "To produce native libraries run: ANDROID_NDK_HOME=/path/to/ndk bash scripts/build-gereh-native.sh"
