package com.wafflestudio.snutt2.views.logged_in.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.DialogTableModifyBinding
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@AndroidEntryPoint
class TableModifyFragment(
    private val tableDto: SimpleTableDto,
    private val onThemeChange: () -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogTableModifyBinding

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var apiOnError: ApiOnError

    private val tableListViewModel: TableListViewModel by activityViewModels()

    private val selectedTimetableViewModel: SelectedTimetableViewModel by activityViewModels()

    var tableTitle: String = tableDto.title // so dirty

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTableModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.changeNameButton.throttledClicks()
//            .observeOn(AndroidSchedulers.mainThread())
//            .flatMapMaybe {
//                dialogController.showTextDialog(
//                    R.string.home_drawer_change_name_dialog_title,
//                    tableTitle,
//                    R.string.home_drawer_change_name_dialog_hint
//                )
//            }
//            .flatMapCompletable {
//                tableTitle = it
//                tableListViewModel.changeNameTable(tableDto.id, it)
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy(onError = apiOnError)

        binding.deleteButton.throttledClicks()
            .doOnComplete {
                dismiss()
            }
            .flatMapMaybe {
                if (tableListViewModel.checkTableDeletable(tableDto.id)) {
                    requireContext().toast("현재 선택된 시간표를 삭제할 수 없습니다.")
                    return@flatMapMaybe Maybe.empty()
                }
                dialogController.showConfirm(
                    R.string.table_delete_alert_title,
                    R.string.table_delete_alert_message
                )
            }
            .flatMapSingle {
                tableListViewModel.deleteTable(tableDto.id)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnEach { dismiss() }
            .subscribeBy(
                onNext = {
                    requireContext().toast("시간표가 삭제되었습니다.")
                },
                onError = apiOnError
            )

        binding.themeButton.throttledClicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    if (tableDto.id == selectedTimetableViewModel.lastViewedTable.get().value?.id) {
                        dismiss()
                        onThemeChange()
                    } else {
                        requireContext().toast("현재 선택된 강좌의 테마만 변경할 수 있습니다.")
                    }
                }
            )
    }
}
