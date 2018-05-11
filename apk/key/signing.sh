#!/bin/sh
#zipalign은 release시에 gradle에서 해줌
#~/Android/Sdk/build-tools/27.0.3/zipalign -v -p 4 $1 $1_aligned.apk
cp ../../app/build/outputs/apk/release/app-release-unsigned.apk .
java -jar signapk.jar platform.x509.pem platform.pk8 app-release-unsigned.apk app-release-signed.apk
~/android-studio/jre/bin/jarsigner -verify -verbose -certs app-release-signed.apk

#apksigner는 검증 결과 출력이 없어서 jarsigner로 확인
#~/Android/Sdk/build-tools/27.0.3/apksigner verify app-release-signed.apk

