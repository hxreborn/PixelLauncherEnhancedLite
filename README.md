# PLE Lite

A lightweight fork of [Pixel Launcher Enhanced](https://github.com/Mahmud0808/PixelLauncherEnhanced).

This is a modified fork distributed under the GNU General Public License v3.0. See [LICENSE](LICENSE) for details.

## Features

- **Icons**
  - Remove shortcut icon badge

- **Home Screen**
  - Lock layout
  - Double tap to sleep (native implementation, requires root)
  - Hide At A Glance
  - Hide desktop search bar

- **Miscellaneous**
  - Hide gesture indicator
  - Navigation bar spacing (adjustable, 0-100%)
  - Entry in launcher settings
  - Entry in homescreen popup
  - Developer options (Pixel Launcher only)
  - Restart launcher

## Requirements

- Rooted device (launcher needs root for double tap to sleep, settings app needs root for restart)
- LSPosed or compatible Xposed framework

## Installation

1. Install the APK
2. Enable the module in LSPosed and scope it to your launcher
3. Grant root access to the launcher
4. Force close and reopen the launcher

## Build

Requires JDK 17+ and Android SDK (API 36).

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Credits

- [DrDisagree](https://github.com/Mahmud0808) - original Pixel Launcher Enhanced
