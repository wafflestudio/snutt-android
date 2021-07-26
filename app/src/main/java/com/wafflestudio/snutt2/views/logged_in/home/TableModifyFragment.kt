package com.wafflestudio.snutt2.views.logged_in.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.DialogTableModifyBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@AndroidEntryPoint
class TableModifyFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogTableModifyBinding

    private lateinit var tableDto: TableDto

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var apiOnError: ApiOnError

    private val tableListViewModel: TableListViewModel by activityViewModels()

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
        binding.changeNameButton.throttledClicks()
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapMaybe {
                dialogController.showTextDialog(
                    R.string.home_drawer_change_name_dialog_title,
                    tableDto.title,
                    R.string.home_drawer_change_name_dialog_hint
                )
            }
            .subscribeBy {
                tableListViewModel.changeNameTable(tableDto.id, it)
                    .subscribeBy(onError = apiOnError)
            }

        binding.deleteButton.throttledClicks()
            .flatMapSingle {
                tableListViewModel.deleteTable(tableDto.id)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = {
                requireContext().toast("시간표가 삭제되었습니다.")
                dismiss()
            }, onError = apiOnError)
    }

    fun show(manager: FragmentManager, tableDto: TableDto) {
        super.show(manager, tableDto.id)
        this.tableDto = tableDto
    }
}
