package com.wafflestudio.snutt2.domain.model

/**
 * 테이블이 참조하는 테마에 대한 정보
 * @see com.wafflestudio.snutt2.domain.ThemeService
 *
 * 여기부턴 논의
 * 분명 도메인에서는 서로 강력하게 연결된 모델들인데, 구현적으로는 서로 거리가 먼 (=연결고리를 찾기 위해 별도 API 가 필요한) 경우가 존재한다.
 * 이럴 때는 OOReference 라는 이름의 필드로 두고, 느슨하게 결합시켜 둔다.
 *
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
