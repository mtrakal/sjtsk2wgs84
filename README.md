# Převod souřadnic ETRS89-TM33N na GPS WGS84

Aplikace pro převod souřadnic ETRS89-TM33N na GPS WGS84.
Data jsou získána
z [Inspire geoportál data výskopisu](https://inspire-geoportal.ec.europa.eu/srv/cze/catalog.search#/metadata/CZ-CUZK-EL_TIN).

Aplikace požaduje jako vstupní soubor GML stažený právě z tohoto portálu.

Výstupem je pak geojson, který je možné zobrazit např. na [geojson.io](https://geojson.io/).

Pro konverzi mezi systémy je použita knihovna [Proj4J](https://github.com/locationtech/proj4j).

Bohužel její stávající verze nepodporuje převod 3D modelu (výskopisu). Tedy výstupní data obsahují výskopis pouze
vstupních dat.

## Poznámka

Aplikace se tváří jako Multiplatform, ale webová část wasmJs není implementována.

Aplikace je přizpůsobena pouze mým účelům pro konkrétní oblast dat (filtruje data výstupu).
Jednoduše lze změnit a vylepšit, ale není to cílem. Sloužila jednorázově.

Aplikaci lze použít i pro konverzi mezi jinými projekčními systémy, když se vhodně drobně upraví parametry.


---
* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.