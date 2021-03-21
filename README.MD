# SNUTT-android

> THIS IS REFACTORING BRANCH

## SETUP

### 1. app/fabric.properties
fabric console 에서 아래 값을 복사
- apiKey=<API_KEY>
- apiSecret=<BUILD_SECRET>

### 2. app/src/live/

#### 2.1 google-services.json
firebase console 에서 다운로드

#### 2.2 res/values/strings.xml 
- app_name: SNUTT
- facebook_app_id: facebook console 에서 복사
- api_key: 서버 통신을 위한 API KEY (개발자에게 문의)
- api_server: api 서버 도메인

### 3. app/src/staging/

#### 3.1 google-services.json
firebase console 에서 다운로드

#### 3.2 res/values/strings.xml 
- app_name: SNUTT_Staging
- facebook_app_id: facebook console 에서 
- api_key: 서버 통신을 위한 API KEY (개발자에게 문의)
- api_server: api 서버 도메인
