package com.wafflestudio.snutt2.views.logged_in.home.reviews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.navigation.NavigationDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userRepository: UserRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<NavigationDestination.Review>()

    val accessToken: StateFlow<String> = userRepository.accessToken

    val reviewId: String = route.reviewId
    val lectureId: String = route.lectureId
    val referrer: String = route.referrer

    private val _uiEvent = MutableSharedFlow<ReviewBottomSheetUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun close() {
        viewModelScope.launch {
            _uiEvent.emit(ReviewBottomSheetUiEvent.NavigateBack)
        }
    }
}

sealed interface ReviewBottomSheetUiEvent {
    data object NavigateBack : ReviewBottomSheetUiEvent
}
