#!/usr/bin/env python3
"""VOYA mobile preflight static checks (not an Android/iOS compilation)."""
import plistlib
import re
import sys
from pathlib import Path
from xml.etree import ElementTree

ROOT = Path(__file__).resolve().parents[1]
errors = []
checks = 0


def check(condition, message):
    global checks
    checks += 1
    if condition:
        print('OK  ' + message)
    else:
        errors.append(message)
        print('FAIL ' + message)


def text(path):
    p = ROOT / path
    check(p.is_file(), f'File exists: {path}')
    return p.read_text(encoding='utf-8') if p.is_file() else ''

settings = text('Android/app/build.gradle.kts')
manifest_file = ROOT / 'Android/app/src/main/AndroidManifest.xml'
check(manifest_file.is_file(), 'Android manifest exists')
manifest = ElementTree.parse(manifest_file).getroot() if manifest_file.exists() else None
check(bool(re.search(r'targetSdk\s*=\s*36\b', settings)), 'Android target SDK 36')
check(bool(re.search(r'compileSdk\s*=\s*36\b', settings)), 'Android compile SDK 36')
check('applicationId = "com.voya.app"' in settings, 'Android package identifier')
java = text('Android/app/src/main/java/com/voya/app/MainActivity.java')
check('https://voya-app.funpony.chatgpt.site/' in java, 'Android VOYA website URL')
check('TRUSTED_HOST.equalsIgnoreCase' in java, 'Android domain allowlist')
if manifest is not None:
    android='{http://schemas.android.com/apk/res/android}'
    perms={p.attrib.get(android+'name') for p in manifest.findall('uses-permission')}
    for p in ['android.permission.INTERNET','android.permission.CAMERA','android.permission.RECORD_AUDIO','android.permission.ACCESS_FINE_LOCATION']:
        check(p in perms, f'Android manifest permission: {p}')

info = ROOT / 'iOS/VOYA/Info.plist'
check(info.exists(), 'iOS Info.plist exists')
plist = plistlib.loads(info.read_bytes()) if info.exists() else {}
for k in ['NSCameraUsageDescription','NSMicrophoneUsageDescription','NSLocationWhenInUseUsageDescription','NSPhotoLibraryUsageDescription']:
    check(bool(plist.get(k)), f'iOS usage description: {k}')
swift = text('iOS/VOYA/VOYABrowserScreen.swift')
check('https://voya-app.funpony.chatgpt.site/' in swift, 'iOS VOYA website URL')
check('WKWebView' in swift and 'WKNavigationDelegate' in swift, 'iOS WKWebView delegate configuration')
text('iOS/VOYA/VOYAApp.swift')
pbx = text('iOS/VOYA.xcodeproj/project.pbxproj')
check('VOYABrowserScreen.swift in Sources' in pbx, 'iOS project includes browser source')
for f in ['Android/app/src/main/assets/offline.html','iOS/VOYA/Resources/offline.html']:
    html = text(f)
    check('NOT A LIVE SERVICE' in html, f'Offline preview clearly labelled: {f}')
check((ROOT/'.github/workflows/mobile-build.yml').is_file(), 'GitHub build workflow exists')
print(f'\n{checks-len(errors)} / {checks} static checks passed.')
if errors:
    print('IMPORTANT: static checks do not replace Gradle/Xcode compilation or on-device tests.')
    sys.exit(1)
print('This preflight does not mean APK, IPA, or production functionality was built.')
