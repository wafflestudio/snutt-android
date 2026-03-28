package com.wafflestudio.snutt2.lib.logging.compose

// 구 BottomSheet 방식에 의존하던 BottomSheetLoggingEffect 는 제거됨.
// 신규 바텀시트 방식에서는 각 BottomSheetLayout의 sheetContent when 분기에서
// LaunchedEffect(Unit) { analyticsLogger.logScreen(...) } 패턴으로 개별 로깅.
