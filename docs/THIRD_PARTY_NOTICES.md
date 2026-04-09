# Third-Party Notices

This project includes third-party libraries. The list below documents dependencies and their typical license families. You are responsible for validating license terms for your exact release before distribution.

## Direct Dependencies (from pom.xml)

1. org.openjfx:javafx-controls:21.0.2
- Project: OpenJFX
- Typical license: GPL v2 with Classpath Exception
- Site: https://openjfx.io

2. org.openjfx:javafx-fxml:21.0.2
- Project: OpenJFX
- Typical license: GPL v2 with Classpath Exception
- Site: https://openjfx.io

3. org.openjfx:javafx-swing:21.0.2
- Project: OpenJFX
- Typical license: GPL v2 with Classpath Exception
- Site: https://openjfx.io

4. org.xerial:sqlite-jdbc:3.45.1.0
- Project: SQLite JDBC
- Typical license: Apache License 2.0 (plus bundled SQLite notices)
- Site: https://github.com/xerial/sqlite-jdbc

5. com.google.zxing:core:3.5.3
- Project: ZXing
- Typical license: Apache License 2.0
- Site: https://github.com/zxing/zxing

6. com.google.zxing:javase:3.5.3
- Project: ZXing
- Typical license: Apache License 2.0
- Site: https://github.com/zxing/zxing

7. com.github.librepdf:openpdf:1.3.43
- Project: OpenPDF
- Typical licenses: dual-license family (LGPL/MPL)
- Site: https://github.com/LibrePDF/OpenPDF

8. org.controlsfx:controlsfx:11.2.1
- Project: ControlsFX
- Typical license: BSD-style
- Site: https://github.com/controlsfx/controlsfx

## Common Transitive Dependencies Included by Build

These may appear in the shaded artifact:

1. org.slf4j:slf4j-api
2. com.beust:jcommander
3. com.github.jai-imageio:jai-imageio-core

Validate their license texts from Maven Central or upstream repositories for your shipped version.

## How to Re-Generate Dependency Inventory

Use Maven dependency tree:

```powershell
mvn dependency:tree -DoutputType=text
```

For machine-readable output (for compliance tooling):

```powershell
mvn dependency:tree -DoutputType=dot
```

## Asset and Content Notice

In addition to code dependencies, this project contains image assets in src/main/resources/images. Ensure you have redistribution permission for:

1. Team photos
2. Logos and seals
3. Any externally sourced graphics

## Disclaimer

This document is informational and not legal advice. Always verify current license texts and obligations for your release artifacts.
