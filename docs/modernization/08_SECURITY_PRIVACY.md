# 08_SECURITY_PRIVACY.md - Security & Privacy Standards

## 1. Component Export Security
- Ensure all Activities, Services (`MusicService`), and BroadcastReceivers (`NotificationReceiver`) in `AndroidManifest.xml` have explicit `android:exported` attributes in accordance with Android 14/15 security requirements.

## 2. Permissions Management
- Audio file access permissions (such as `READ_EXTERNAL_STORAGE` / `READ_MEDIA_AUDIO`) must be requested gracefully using modern permission APIs (`ActivityResultContracts.RequestPermission`).

## 3. Data Protection
- Secure local storage (`SharePreference` and Room DB) against unauthorized access.
- Never log sensitive user data, auth tokens, or PII.
