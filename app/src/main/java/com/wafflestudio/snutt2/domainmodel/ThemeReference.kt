package com.wafflestudio.snutt2.domainmodel

/**
 * 테이블이 참조하는 테마에 대한 정보
 */
sealed interface ThemeReference {
    /**
     * 내장 테마 참조
     * @param code 내장 테마 코드 (0=SNUTT, 1=모던, 2=가을, 3=벚꽃, 4=얼음, 5=잔디)
     */
    data class BuiltIn(val code: Int) : ThemeReference

    /**
     * 커스텀 테마 참조
     * @param themeId 커스텀 테마 ID
     */
    data class Custom(val themeId: String) : ThemeReference
}