# SNUTT-android

## SETUP

### app/fabric.properties
fabric console 에서 아래 값을 복사
- apiKey=<API_KEY>
- apiSecret=<BUILD_SECRET>

### app/src/live/

#### google-services.json
firebase console 에서 다운로드

#### res/values/strings.xml 
- app_name: SNUTT
- facebook_app_id: facebook console 에서 복사
- api_key: 서버 통신을 위한 API KEY (개발자에게 문의)
- api_server: api 서버 도메인

### app/src/staging/

#### google-services.json
firebase console 에서 다운로드

#### res/values/strings.xml 
- app_name: SNUTT_Staging
- facebook_app_id: facebook console 에서 
- api_key: 서버 통신을 위한 API KEY (개발자에게 문의)
- api_server: api 서버 도메인
