![Banner](https://voiid-studios.github.io/stuff/assets/project/TsunamiLib/banner.png)

<p align="center" style="text-align: center;">
  <a href="https://ko-fi.com/maxxvoiid/donate"><img src="https://voiid-studios.github.io/stuff/assets/buttons/misc/support_kofi/cozy.svg" alt="Support us on Ko-fi" style="margin: 5px 10px;"></a>
  <a href="https://modrinth.com/plugin/tsunamilib"><img src="https://voiid-studios.github.io/stuff/assets/buttons/available/modrinth/cozy.svg" alt="Download on Modrinth" style="margin: 5px 10px;"></a>
  <br><a href="https://voiidstudios.pages.dev/jd/tsunamilib/"><img src="https://voiid-studios.github.io/stuff/assets/buttons/misc/read_jdocs/compact.svg" alt="View the TsunamiLib JDocs" style="margin: 5px 10px;"></a>
  <a href="https://choosealicense.com/licenses/mit/"><img src="https://voiid-studios.github.io/stuff/assets/buttons/license/mit_compact.svg" alt="View the MIT License" style="margin: 5px 10px;"></a>
  <a href="https://github.com/Voiid-Studios/tsunamilib/issues/new/choose"><img src="https://voiid-studios.github.io/stuff/assets/buttons/misc/report_bugs/mini.svg" alt="Report Bugs on GitHub" style="margin: 5px 10px;"></a>
  <a href="https://github.com/Voiid-Studios/tsunamilib"><img src="https://voiid-studios.github.io/stuff/assets/buttons/available/github/mini.svg" alt="View Source Code on GitHub" style="margin: 5px 10px;"></a>
</p>

![Divider](https://voiid-studios.github.io/stuff/assets/project/TsunamiLib/divider.png)

## 📖 About

TsunamiLib (TL) is a library plugin developed by Voiid Studios to provide shared utilities and systems for plugins that integrate with it.

TsunamiLib does not add gameplay content or standalone features to a server. Instead, it provides reusable functionality that dependent plugins can use, helping keep common systems consistent between projects.

For server administrators, TsunamiLib only needs to be installed when a plugin requires it as a dependency. Plugins that depend on TsunamiLib may not function correctly without the library installed.

For developers, TsunamiLib currently provides [JDocs](https://voiidstudios.pages.dev/jd/tsunamilib/) covering its available classes and methods. The library is open source, and its implementation can also be explored through the [TsunamiLib source code.](https://github.com/Voiid-Studios/tsunamilib)

![Divider](https://voiid-studios.github.io/stuff/assets/project/TsunamiLib/divider.png)

## ✨ Features
- 🧩 **Reusable Library:** Provides common utilities and systems that plugin developers can integrate into their own projects.
- 🖥️ **Platform Support:** Provides platform detection and scheduling utilities for Paper, Spigot, and Folia-compatible environments.
- 🎨 **Adventure-Powered Messages:** Provides a YAML-based message system with multiple-language support and per-plugin prefixes.
- 🖼️ **TsunamiGui:** Provides reusable inventory GUI components, including pagination, scrolling, storage interfaces, item builders, and NBT utilities.
- 📋 **Structured Logging:** Provides a unified logging system for plugins using TsunamiLib.
- 🔄 **Update Checking:** Provides update-checking functionality that dependent plugins can use to check for newer versions.
- 📦 **Lightweight Foundation:** Provides shared functionality without adding gameplay content to the server.

![Divider](https://voiid-studios.github.io/stuff/assets/project/TsunamiLib/divider.png)

## 📦 Installation

1. Download the latest version.
2. Place the downloaded `.jar` file in your server's `plugins/` folder
3. Restart the server, TsunamiLib is ready! 🌊

![Divider](https://voiid-studios.github.io/stuff/assets/project/TsunamiLib/divider.png)

## 📚 Voiid Studios projects using TsunamiLib

TsunamiLib is currently used internally by Voiid Studios and is planned to be integrated into additional projects.

| Plugin Name           | Status        | Planned Use                                                      |
| --------------------- | ------------- | ---------------------------------------------------------------- |
| [WonderEvents](https://modrinth.com/plugin/wonderevents)          | ✅ Required | Starting with WonderEvents 26.10.0, TsunamiLib is a required dependency.        |
| Voiid Countdown Timer | ⚠ In Development | Currently being reworked to use TsunamiLib for shared functionality.        |
| DynamicAPI            | ⚠ Coming Soon | Use TsunamiLib for shared functionality where applicable.        |
| Waiting Screen        | ❌ Not Planned | Uses a different implementation and does not require TsunamiLib. |
| QuestMaster           | ❌ Not Planned | Uses a different implementation and does not require TsunamiLib. |

![Divider](https://voiid-studios.github.io/stuff/assets/project/TsunamiLib/divider.png)

## ⚡ fastStats
<a href="https://faststats.dev/project/tsunami/stats"><img src="https://faststats.dev/embed/a2facd86-aade-4640-8404-31cf9fd49d02.svg?w=960&h=340&theme=dark" alt="Servers & Players"></a>
