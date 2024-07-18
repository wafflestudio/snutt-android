package com.wafflestudio.snutt2.core.network.util

object ErrorCode {
    const val SERVER_FAULT = 0x0000

    /* 400 - Bad request */
    const val INVALID_EMAIL = 0x300F
    const val VACANCY_PREV_SEMESTER = 0x9C45
    const val VACANCY_DUPLICATE = 0x9FC4
    const val INVALID_NICKNAME = 0x9C48

    /* 401 - Request was invalid */
    const val NO_FB_ID_OR_TOKEN = 0x1001
    const val NO_YEAR_OR_SEMESTER = 0x1002
    const val NOT_ENOUGH_TO_CREATE_TIMETABLE = 0x1003
    const val NO_LECTURE_INPUT = 0x1004
    const val NO_LECTURE_ID = 0x1005
    const val ATTEMPT_TO_MODIFY_IDENTITY = 0x1006
    const val NO_TIMETABLE_TITLE = 0x1007
    const val NO_REGISTRATION_ID = 0x1008
    const val INVALID_TIMEMASK = 0x1009
    const val INVALID_COLOR = 0x100A
    const val NO_LECTURE_TITLE = 0x100B
    const val EXPIRED_PASSWORD_RESET_CODE = 0x2010
    const val WRONG_PASSWORD_RESET_CODE = 0x2011

    /* 403 - Authorization-related */
    const val WRONG_API_KEY = 0x2000
    const val NO_USER_TOKEN = 0x2001
    const val WRONG_USER_TOKEN = 0x2002
    const val NO_ADMIN_PRIVILEGE = 0x2003
    const val WRONG_ID = 0x2004
    const val WRONG_PASSWORD = 0x2005
    const val WRONG_FB_TOKEN = 0x2006
    const val UNKNOWN_APP = 0x2007

    /* 403 - Restrictions */
    const val INVALID_ID = 0x3000
    const val INVALID_PASSWORD = 0x3001
    const val DUPLICATE_ID = 0x3002
    const val DUPLICATE_TIMETABLE_TITLE = 0x3003
    const val DUPLICATE_LECTURE = 0x3004
    const val ALREADY_LOCAL_ACCOUNT = 0x3005
    const val ALREADY_FB_ACCOUNT = 0x3006
    const val NOT_LOCAL_ACCOUNT = 0x3007
    const val NOT_FB_ACCOUNT = 0x3008
    const val FB_ID_WITH_SOMEONE_ELSE = 0x3009
    const val WRONG_SEMESTER = 0x300A
    const val NOT_CUSTOM_LECTURE = 0x300B
    const val LECTURE_TIME_OVERLAP = 0x300C
    const val IS_CUSTOM_LECTURE = 0x300D
    const val EMAIL_NOT_VERIFIED = 0x3011

    /* 404 - NOT found */
    const val TAG_NOT_FOUND = 0x4000
    const val TIMETABLE_NOT_FOUND = 0x4001
    const val LECTURE_NOT_FOUND = 0x4002
    const val REF_LECTURE_NOT_FOUND = 0x4003
    const val USER_NOT_FOUND = 0x4004
    const val COLORLIST_NOT_FOUND = 0x4005
    const val EMAIL_NOT_FOUND = 0x4006
}