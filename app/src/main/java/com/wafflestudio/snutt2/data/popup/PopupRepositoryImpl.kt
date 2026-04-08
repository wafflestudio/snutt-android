package com.wafflestudio.snutt2.data.popup

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.Popup
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.GetPopupResults
import com.wafflestudio.snutt2.network.error.toDomainError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopupRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : PopupRepository {

    private val _popups = MutableStateFlow<List<Popup>?>(null)
    override val popups: StateFlow<List<Popup>?> = _popups.asStateFlow()

    override suspend fun ensurePopupsFetched(): Result<Unit> {
        if (_popups.value != null) return Result.Success(Unit)
        return try {
            val visiblePopups = api._getPopup().popups
                .filter { popup ->
                    val expireMillis: Long? = storage.shownPopupIdsAndTimestamp.get()[popup.key]
                    val currentMillis = System.currentTimeMillis()
                    expireMillis == null || currentMillis >= expireMillis
                }
                .map { it.toDomain() }
            _popups.value = visiblePopups
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun closePopupWithHiddenDays(): Result<Unit> {
        return try {
            val popup = _popups.value?.firstOrNull()
            if (popup != null) {
                val expiredDay: Long = popup.hideDays?.let { hideDays ->
                    System.currentTimeMillis() + TimeUnit.DAYS.toMillis(hideDays.toLong())
                } ?: INFINITE_LONG_MILLIS

                storage.shownPopupIdsAndTimestamp.update(
                    storage.shownPopupIdsAndTimestamp.get()
                        .toMutableMap()
                        .also { it[popup.key] = expiredDay },
                )

                _popups.value = _popups.value?.drop(1)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun closePopup(): Result<Unit> {
        return try {
            _popups.value = _popups.value?.drop(1)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    companion object {
        const val INFINITE_LONG_MILLIS = Long.MAX_VALUE
    }
}

private fun GetPopupResults.Popup.toDomain(): Popup = Popup(
    key = key,
    imageUri = imageUri,
    linkUrl = linkUrl,
    hideDays = popupHideDays,
)
