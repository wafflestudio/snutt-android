package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableTheme

/**
 * @see com.wafflestudio.snutt2.domainmodel.ThemeReference
 *
 * 여기부턴 논의
 * Table 도메인 모델에서 ThemeReference 라는 필드로 느슨하게 표현되어 있는 TableTheme 의 구체적인 값을 구해다준다.
 * "Domain Service" 계층이라고 이름붙이면 좋을듯? UseCase 를 대체하게 될 계층.
 */
interface ThemeService {
    /**
     * Table 모델의 구체적인 TableTheme 얻기
     */
    suspend fun resolveTheme(table: Table): TableTheme
}