# Minecraft Fun Facts – Android Auto (Version 1.0, Quellprojekt)

Android-App (API 28+) mit einer Media3-Medienbibliothek, Android-Auto-Medienintegration, 40 offline gespeicherten deutschen Fakten und zugehörigen WAV-Sprachaufnahmen.

## Installation / APK bauen
1. Projektordner in Android Studio öffnen (JDK 17 und Android SDK 36 erforderlich).
2. Gradle synchronisieren lassen. **Build > Build APK(s)** ausführen.
3. APK aus `app/build/outputs/apk/debug/app-debug.apk` auf Android installieren.
4. Im Smartphone die App öffnen, den Wiedergabemodus einstellen. Anschließend Android Auto verbinden und nach "Minecraft Fun Facts" suchen.
5. Für einen Fahrzeugtest den Android Auto Desktop Head Unit (DHU) Emulator verwenden.

## Funktionen
- Native Android-App mit Start/Pause, Nächster/Vorheriger Fakt und Kategorien.
- In Android Auto MediaLibraryService mit Medienbrowser und Standard-Mediensteuerung.
- Umschaltbare automatische oder manuelle Wiedergabe über Smartphone-Einstellung.
- Offline-Audio mit deutscher eSpeak-Sprachsynthese; kein Internet oder API-Key erforderlich.
- Zufällig gemischte Reihenfolge pro Durchlauf; am Ende automatisch neu gemischte Liste im Automatikmodus.

## Hinweise und bekannte Grenzen
- Die Audio-Dateien wurden mit einer einfachen synthetischen Stimme erzeugt; für natürlichere Aussprache lassen sich die WAV-Dateien bei unverändertem Namen ersetzen.
- Kategorien im Android-Auto-Browser sind bereitgestellt, Android-Auto-Anzeige kann je nach Host variieren.
- Nicht auf einem echten Fahrzeug bzw. durch Gradle-Compile getestet; eventuell sind Anpassungen an SDK-/Gradle-Versionen nötig.
- Die 40 Fakten sind eine erste redaktionelle Auswahl und keine ursprünglich avisierten 200.
- Die App ist ein inoffizielles Fanprojekt, nicht mit Mojang oder Microsoft verbunden. Keine fremden Assets beigefügt.
- Öffentliche Play-Store-Veröffentlichung benötigt Automotive-Medienrichtlinien-Compliance und Prüfung durch Google.

## Projektstruktur
- `Facts.kt` – lokale Fakten & MediaItems.
- `FunFactService.kt` – Media3 MediaLibraryService / Wiedergabe.
- `MainActivity.kt` – Smartphone-Bedienung und Einstellungen.
- `assets/facts.json` – Fakten; `assets/audio/` – WAV-Sprachaufnahmen.

## Anpassung
Weitere Fakten als JSON-Eintrag in `assets/facts.json` und passende `assets/audio/<id>.wav` hinzufügen. Bereits erzeugte Audioaufnahmen können durch hochwertigere deutsche Sprecherdateien ersetzt werden.
