# GitHub Release Checklist

Use this checklist when publishing a release to GitHub.

## 1. Versioning

1. Update version in pom.xml to the release version (example: 1.0.1).
2. Update any version labels in docs if needed.
3. Commit version changes.

## 2. Build Validation

1. Build the app:
- mvn -DskipTests package
2. Build installer:
- .\build-installer.ps1 -CleanOutput
3. Verify installer launches and app starts successfully.
4. Verify key flows:
- Login
- CRUD modules
- Export PDF
- QR generation and scan

## 3. Signing (Recommended)

If certificate is available, sign installer:

1. PFX mode:
- .\build-installer.ps1 -SkipBuild -CleanOutput -SignInstaller -CertFilePath "C:\path\cert.pfx" -CertPassword "***"

2. Thumbprint mode:
- .\build-installer.ps1 -SkipBuild -CleanOutput -SignInstaller -CertThumbprint "THUMBPRINT"

## 4. Compliance Docs

Ensure these files are up to date:

1. LICENSE
2. README.md
3. docs/THIRD_PARTY_NOTICES.md
4. docs/OSS_SIGNING_CHECKLIST.md
5. docs/SYSTEM_DOCUMENTATION.md

## 5. Prepare Release Assets

Recommended attachments:

1. dist/Athlete Track-<version>.exe
2. checksums.txt (SHA256)
3. Optional: release notes PDF/markdown

Generate checksum:

```powershell
Get-FileHash "dist\Athlete Track-1.0.1.exe" -Algorithm SHA256
```

## 6. Create GitHub Release

1. Create annotated git tag:
- git tag -a v1.0.1 -m "Athlete Track v1.0.1"
2. Push tag:
- git push origin v1.0.1
3. Open GitHub Releases and draft new release from tag.
4. Upload installer and checksum.
5. Paste release notes.
6. Publish release.

## 7. Suggested Release Notes Template

1. Summary
- What changed in this release.

2. New Features
- Feature bullets.

3. Fixes
- Bugfix bullets.

4. Packaging
- Installer name and signing status.

5. Upgrade Notes
- Data compatibility and backup notes.

## 8. Post-Release Validation

1. Download installer from release page on a clean machine.
2. Install and run smoke test.
3. Confirm app metadata and version.
4. Confirm no startup crash.
5. Log any issue and patch quickly.
