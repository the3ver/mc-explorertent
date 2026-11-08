# Explorer Tent (mc-explorertent) – Agent Guidelines

Eigenständiger Fabric-Mod mit dem Entdeckerzelt für Minecraft 26.3 und Java 25. Herausgelöst aus WorldExplorer (`mc-3ver`); WorldExplorer bündelt diesen Mod ab 0.13.0 per Jar-in-Jar.

## Tech Stack
- **Spiel:** Minecraft 26.3 (Java Edition)
- **Mod Loader:** Fabric Loader (>= 0.20.0) + Fabric API
- **Sprache/Runtime:** Java 25
- **Build-System:** Gradle (Wrapper `gradlew` / `gradlew.bat`)

## Commands
- Prüfen (Format, Error Prone, Tests, Architektur- und Ressourcentests, Semgrep, knappe Ausgabe): `bash scripts/check.sh`. Läuft lokal auch als Claude-Stop-Hook.
- Projektregeln stehen in `.semgrep/rules.yml`. Funde beheben; nur begründete Ausnahmen mit `// nosemgrep: <regel-id> <Grund>`.
- Formatieren: `./gradlew spotlessApply` (palantir-java-format). Lokal formatiert ein Claude-Hook jede bearbeitete Java-Datei.
- Teststärke messen: `bash scripts/mutation-report.sh --run` (PIT auf `*Logic`). Überlebende Mutanten zeigen Verhalten, das kein Test absichert.
- Generierte Daten: `./gradlew runDatagen` schreibt nach `src/main/generated` (Provider in `src/datagen/java`). Diese JSONs nie von Hand ändern, sondern den Provider; Ausgabe mitcommitten, CI prüft den Gleichstand.
- Version setzen: `bash scripts/bump-version.sh X.Y.Z` (beim Release mit `--docs`). Einzige Quelle ist `gradle.properties`.
- Build: `./gradlew build`
- Run Client: `./gradlew runClient`. Der feste Name `--username Frank` in `build.gradle` bleibt drin: seit 26.3 hängt der Einzelspieler-Spielstand an der Offline-UUID des Namens.

## Alte IDs
- Bis WorldExplorer 0.12 hießen alle Zeltteile `mc3ver:*`. `TentLegacyIds` leitet sie per Registry-Alias auf `explorertent:*` um. Die Aliase bleiben dauerhaft. Neue Blöcke, Items oder Block-Entities brauchen keinen Alias, umbenannte schon.
- `LegacyIdMigrationTest` läuft über `fabric-loader-junit` im Fabric-Loader, damit die Fabric-API-Mixins greifen.

## Git
- `main` ist per Ruleset geschützt (`.github/rulesets/main.json`): Änderungen nur per Pull Request, Checks `build` und `semgrep` müssen grün sein. Arbeit auf einem Branch committen, pushen, PR öffnen.
- Nach dem Öffnen eines PR in Claude Desktop ohne Rückfrage Auto-fix einschalten (`set_monitor` mit `auto_fix` und `address_comments`). Mergen entscheidet Frank.
- Release: Tag `vX.Y.Z` startet `release.yml`. Modrinth und CurseForge laufen nur, wenn die Repository-Variablen `MODRINTH_ID` und `CURSEFORGE_ID` sowie die Secrets `MODRINTH_TOKEN` und `CURSEFORGE_TOKEN` gesetzt sind.
