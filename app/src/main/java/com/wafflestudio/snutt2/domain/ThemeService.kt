package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableTheme

interface ThemeService {
    /**
     * Table 모델의 구체적인 TableTheme 얻기
     */
    suspend fun resolveTheme(table: Table): TableTheme
}