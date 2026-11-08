#!/usr/bin/env bash
# Setzt die Mod-Version. Einzige Quelle ist gradle.properties; fabric.mod.json liest sie beim Build.
# Aufruf: scripts/bump-version.sh <neue-version> [--docs]
#   --docs zieht README.md mit (erst beim Release).
# Die CHANGELOG-Eintraege schreibt weiterhin ein Mensch oder Agent; das Skript legt die Ueberschriften an und zeigt einen git-cliff-Entwurf.
set -euo pipefail
cd "$(dirname "$0")/.."

new=${1:-}
[[ $new =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]] || { echo "Aufruf: $0 X.Y.Z [--docs]" >&2; exit 1; }
old=$(sed -n 's/^version=//p' gradle.properties)
[ "$old" != "$new" ] || { echo "Version ist bereits $new" >&2; exit 1; }
old_re=${old//./\\.}

sed -i "s/^version=.*/version=$new/" gradle.properties

today=$(date +%F)
for log in CHANGELOG.md CHANGELOG_EN.md; do
  grep -q "^## \[$new\]" "$log" && continue
  # Neue Ueberschrift vor den ersten Versionseintrag setzen
  sed -i "0,/^## \[/s//## [$new] - $today\n\n## [/" "$log"
done

if [ "${2:-}" = "--docs" ]; then
  sed -i "s/explorertent-$old_re\.jar/explorertent-$new.jar/g" README.md
fi

echo "Version $old -> $new"
if command -v uvx >/dev/null; then
  echo
  echo "Rohentwurf aus den Commits seit v$old (cliff.toml), als Vorlage für die Changelog-Texte:"
  uvx git-cliff --unreleased 2>/dev/null
fi
rest=$(git grep -nF "$old" -- . ':!CHANGELOG*.md' ':!settings.gradle' || true)
[ -z "$rest" ] || { echo "Alte Version steht noch in:"; echo "$rest"; }
