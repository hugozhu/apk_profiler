#!/bin/sh

echo "building unsigned release apk ...\n"
ant release

echo "signing apk ...\n"
java -jar signapk/signapk.jar  signapk/platform.x509.pem signapk/platform.pk8 bin/apk_profiler-release-unsigned.apk bin/apk_profiler-release-signed.apk

echo "installing apk ..."
adb install -r bin/apk_profiler-release-signed.apk

echo "starting apk...\n"
adb shell am start -n com.github.hugozhu.profiler/.MainActivity
