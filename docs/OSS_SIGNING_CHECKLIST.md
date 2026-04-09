# OSS Signing Checklist (SignPath Preparation)

Use this checklist before applying for SignPath Foundation OSS signing.

## 1. Licensing

- Add an OSI-approved license at repository root.
- Confirm license covers the codebase.
- Verify third-party assets are allowed to be redistributed.

Current project status:
- License file present: LICENSE (MIT)

## 2. Public Repository Requirements

- Source code should be publicly accessible (for OSS route).
- Project should be actively maintained (recent commits and releases).
- Signed artifact should correspond to a published release.

## 3. Release and Documentation Requirements

- Provide a public release artifact page.
- Document application purpose and behavior.
- Include setup and usage docs.

Current project docs:
- docs/SYSTEM_DOCUMENTATION.md
- docs/USER_MANUAL.md
- docs/DEPLOYMENT_CHECKLIST.md
- docs/DOCUMENTATION_PACK.md

## 4. Security and Reputation

- Build installer from tagged source.
- Keep reproducible build steps in repository.
- Avoid bundling unknown binaries.

## 5. Packaging Readiness

- Build command:
  - .\build-installer.ps1 -CleanOutput
- Installer output:
  - dist/Athlete Track-1.0.exe

## 6. Optional: Bring Your Own Certificate (BYOC)

If using your own certificate, OSS Foundation eligibility constraints are not required.

Signing examples with the existing script:
- PFX file:
  - .\build-installer.ps1 -CleanOutput -SignInstaller -CertFilePath "C:\path\cert.pfx" -CertPassword "your-password"
- Cert thumbprint:
  - .\build-installer.ps1 -CleanOutput -SignInstaller -CertThumbprint "THUMBPRINT"

## 7. Pre-Submission Final Check

- License file exists and is correct.
- Public repository and releases are available.
- Installer builds cleanly from current source.
- Docs are complete and public.
- Asset rights are verified.

## 8. Notes About Assets

This project includes team photos and organization-related image assets.
Before public OSS release, ensure you have permission to publish and redistribute:
- team photos
- logos and seals
- any externally sourced design assets
