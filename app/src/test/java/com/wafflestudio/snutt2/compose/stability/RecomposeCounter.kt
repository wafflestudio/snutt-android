package com.wafflestudio.snutt2.compose.stability

import androidx.compose.runtime.Stable

// Recompose 카운터 계측 테스트용 공용 홀더.
// 반드시 @Stable 클래스여야 한다 — var를 캡처하는 람다는 unstable이라 부모 recompose마다
// 새 인스턴스가 만들어져 측정 대상 Composable의 skip 자체를 깨뜨린다.
@Stable
internal class RecomposeCounter {
    var count = 0
        private set

    fun increment() {
        count++
    }
}
