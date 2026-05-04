# Karna Notes

Android App built with kotlin and App Compact. Android material are optional. Pure using usb debugging and from scratch template from [https://github.com/catc0de1/kotlin-minimal-starter](https://github.com/catc0de1/kotlin-minimal-starter.git)

---

## Features

- Create note for spesific task using strict format
- Validation on create or update item
- Editable and deletable item
- Excel column UI/UX style
- Internal storage app with *Room*
- Manual backup (export) with `.csv` format
- Import data with `.csv` format

**Format Data**:
|Column |Value                                      |
|-------|-------------------------------------------|
|ID     |Imuttable, Auto-generate                   |
|Name   |String, Required                           |
|Date   |String, ISO Format, Default = current date |
|Address|String, Required                           |
|Price  |Int, Default = 0                           |
|Status |String, Default = "-"                      |

---

## Spesification

**Minimum Spec**:
- Version: Android 8.0 (Oreo)
- Storage: > 10 MB

**Recommended Spec**:
- Version: Android 14

---

## Installation

1. **Download APK File**

    Download APK file on [Drive](https://drive.google.com/drive/folders/1lIhQGIfjypRPGX33OD3Y5Zvc9adG1vGN?usp=sharing) or my [GitHub](https://github.com/catc0de1/kotlin-note-mobile/tags)

    Choose version of apk and allow download from this site.

2. **Deactive Android Security for a While**

    This application is independent deployment, not from PlayStore or AppStore. So inactive Android security for **temporary**.

3. **Install Application**

    Install application after deactive Android security. After installation success, **Don't forget to turn on Android Security**

---

## Development Environment Setup

Read environment setup in my blog [here](https://zblogzone.netlify.app/blog/tutorial/kotlin-cli-only) or from [starter](https://github.com/catc0de1/kotlin-minimal-starter).

---

## License

This project [Stritch Licensed](https://github.com/catc0de1/kotlin-note-mobile?tab=MIT-1-ov-file)
