#!/usr/bin/env bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
./gradlew assembleDebug "$@"
APK="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK" ]; then
    echo ""
    echo "APK: $(realpath $APK)"
    echo "Size: $(du -h $APK | cut -f1)"
fi
