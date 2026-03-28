package com.wafflestudio.snutt2.data.popup

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.toDomainError
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopupRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
    private val popupState: PopupState,
) : PopupRepository {

    override suspend fun fetchAndSetPopup(): Result<Unit> {
        return try {
            val popups = api._getPopup().popups.filter {
                val expireMillis: Long? = storage.shownPopupIdsAndTimestamp.get()[it.key]
                val currentMillis = System.currentTimeMillis()

                (expireMillis == null || currentMillis >= expireMillis)
            }
            popupState.popup = popups
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun closePopupWithHiddenDays(): Result<Unit> {
        return try {
            val popup = popupState.popup.firstOrNull()
            if (popup != null) {
                val expiredDay: Long = popup.popupHideDays?.let { hideDays ->
                    System.currentTimeMillis() + TimeUnit.DAYS.toMillis(hideDays.toLong())
                } ?: INFINITE_LONG_MILLIS

                storage.shownPopupIdsAndTimestamp.update(
                    storage.shownPopupIdsAndTimestamp.get()
                        .toMutableMap()
                        .also {
                            it[popup.key] = expiredDay
                        },
                )

                popupState.popup = popupState.popup.drop(1)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun closePopup(): Result<Unit> {
        return try {
            popupState.popup = popupState.popup.drop(1)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    companion object {
        const val INFINITE_LONG_MILLIS = Long.MAX_VALUE
    }
}
