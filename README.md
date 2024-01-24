[![CI](https://github.com/wafflestudio/SNUTT-android/actions/workflows/ci.yml/badge.svg)](https://github.com/wafflestudio/SNUTT-android/actions/workflows/ci.yml)
[![CD](https://github.com/wafflestudio/SNUTT-android/actions/workflows/cd.yml/badge.svg)](https://github.com/wafflestudio/SNUTT-android/actions/workflows/cd.yml)
<div align="center">
  <a href="https://github.com/wafflestudio/snutt-ios">
    <img src="https://user-images.githubusercontent.com/33917774/199519767-60590904-b15a-4464-ab21-e3a424649d5c.svg" alt="Logo" width="70" height="70">
  </a>
  <h3 align="center">SNUTT Android</h3>
  <p align="center">
    <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
    <img src="https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white" />
    <img src="https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black" />
    <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white" />
  </p>
</div>

## Features
- MVVM
- Jetpack Compose
- Dependency Injection with Hilt
- Firebase crashylistics & app distribution

## Getting Started
### Secrets
- `app/src/staging/google-services.json`
- `app/src/staging/res/value/secrets.xml`
- 필요시 maintainer 에게 요청하기
### Installation
```
git clone https://github.com/wafflestudio/snutt-android
```
### Deployment
1. develop에 모든 PR을 머지하고 release-${version-code} 브랜치를 생성합니다.
2. version.properties의 version code를 rc를 포함하여 입력하고(e.g. 3.4.0-rc1) 푸쉬하면 cd.yml에 따라 app distribution에 apk를 업로드되며, 테스트를 거치며 rc 버전을 올립니다.
3. 테스트가 완료되면 version code를 정식 버전으로 입력하고 푸쉬합니다. app distribution에 업로드된 apk를 플레이스토어에 등록합니다.
4. release-${version-code} 브랜치를 develop에 머지합니다. 출시된 버전이 가장 최근의 release 브랜치와 동일하도록 유지해주세요.

## Branch Conventions
- default branch: `develop`
- PR 브랜치 명칭: `${username}/${changes}` (e.g. `sanggggg/renewal-table-ui`)
- merge convention: only squash merge

## Maintainer
- **Current** @JuTaK97
