# Explorer Tent

Ein tragbares Entdeckerzelt für Minecraft 26.3 (Fabric). Aufstellen, darin schlafen, ohne den Heimat-Spawnpunkt zu verschieben, und wieder einpacken. Die Truhe im Zelt nimmt ihren Inhalt beim Einpacken mit, der Inhalt des Ofens fällt auf den Boden.

![Entdeckerzelt](docs/assets/explorer_tent.png)

Das Zelt stammt aus [WorldExplorer](https://github.com/the3ver/mc-3ver). WorldExplorer liefert diesen Mod ab Version 0.13.0 mit. Wer nur das Zelt will, installiert ihn einzeln.

## Installation

1. Fabric Loader (>= 0.20.0) für Minecraft 26.3 installieren.
2. [Fabric API](https://modrinth.com/mod/fabric-api/versions?g=26.3) in den `mods`-Ordner legen.
3. `explorertent-0.1.0.jar` aus den [Releases](https://github.com/the3ver/mc-explorertent/releases/latest) in den `mods`-Ordner legen.

## Umstieg von WorldExplorer

Bis WorldExplorer 0.12 war das Zelt fest eingebaut und hieß intern `mc3ver:*`. Dieser Mod lädt die alten IDs weiter.

| Situation | Was passiert |
|---|---|
| WorldExplorer auf 0.13.0 oder neuer aktualisieren | Nichts zu tun. Aufgestellte und gepackte Zelte bleiben samt Truhen- und Ofeninhalt. |
| WorldExplorer entfernen, nur das Zelt behalten | Zelte bleiben. Hain, Birnbäume und alle anderen WorldExplorer-Inhalte gehen verloren. Vorher ein Backup der Welt anlegen. |
| WorldExplorer 0.12 oder älter zusammen mit diesem Mod | Das Spiel startet nicht und meldet, dass WorldExplorer aktualisiert werden muss. |
| WorldExplorer 0.13 plus ein neuerer Explorer Tent einzeln | Fabric lädt die neuere Zelt-Version. |

Verloren geht nur der Fortschritt im Rezeptbuch für die Zeltrezepte, weil die Rezepte neue IDs haben.

## Entwicklung

```bash
bash scripts/check.sh
```

```bash
./gradlew runClient
```

Weitere Befehle stehen in [AGENTS.md](AGENTS.md).

## Lizenz

MIT, siehe [LICENSE](LICENSE).
