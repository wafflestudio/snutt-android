package com.wafflestudio.snutt2.network.error

object ErrorCode {
    // 에러 메시지 문구 통일 후 남은 코드
    const val SERVER_FAULT = 0x0000
    const val NO_NETWORK = 0x0001

    const val LECTURE_TIME_OVERLAP = 0x300C
    const val INVALID_EMAIL = 0x300F
    const val PAST_SEMESTER = 0x9C58
    const val DISABLED_FEATURE = 0x9C59
    // Client-Side 에러 코드 (추후 추가)

    // 리팩토링 후 코드 형태를 위해 남겨놓은 에러 코드
    const val WRONG_API_KEY = 0x2000
    const val NO_USER_TOKEN = 0x2001
    const val WRONG_USER_TOKEN = 0x2002
    const val NO_ADMIN_PRIVILEGE = 0x2003
    const val WRONG_PASSWORD = 0x2005

    const val INVALID_ID = 0x3000
    const val INVALID_PASSWORD = 0x3001
    const val DUPLICATE_ID = 0x3002

    const val USED_EMAIL = 0x9FC5
}
