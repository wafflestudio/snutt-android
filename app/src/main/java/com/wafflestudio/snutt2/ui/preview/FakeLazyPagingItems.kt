package com.wafflestudio.snutt2.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Preview 전용 [LazyPagingItems] 헬퍼.
 *
 * [LazyPagingItems] 의 생성자가 internal 이라 직접 인스턴스화할 수 없고,
 * `flowOf(PagingData.from(...)).collectAsLazyPagingItems()` 는 IDE preview 의 정적 환경에서
 * 비동기 collect 가 한 frame 내에 완료되지 않아 `itemCount = 0` 으로 남는 문제가 있다.
 *
 * 해결: paging-compose 내부 구현은 flow 가 [kotlinx.coroutines.flow.SharedFlow] 인 경우
 * `replayCache.firstOrNull()` 를 cached PagingData 로 사용해 presenter 를 즉시 초기화한다.
 * [MutableStateFlow] 는 SharedFlow 의 서브타입이므로, 이를 통해 preview 환경에서도
 * 동기적으로 itemCount 가 채워진 [LazyPagingItems] 를 얻을 수 있다.
 *
 * 참조: paging-compose 공식 sample `PagingPreview` 가 동일 패턴을 사용한다.
 */
@Composable
fun <T : Any> rememberFakeLazyPagingItems(items: List<T>): LazyPagingItems<T> {
    val flow = remember(items) { MutableStateFlow(PagingData.from(items)) }
    return flow.collectAsLazyPagingItems()
}

/**
 * 빈 목록을 표현하는 preview 용 [LazyPagingItems].
 *
 * Placeholder/Empty 분기 preview 에서 사용한다.
 */
@Composable
fun <T : Any> rememberEmptyFakeLazyPagingItems(): LazyPagingItems<T> {
    val flow = remember { MutableStateFlow(PagingData.empty<T>()) }
    return flow.collectAsLazyPagingItems()
}
