package com.wafflestudio.snutt2.views.logged_in.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.databinding.DialogSelectThemeBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@AndroidEntryPoint
class TableThemeSheet(
    private val tableDto: SimpleTableDto,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSelectThemeBinding

    @Inject
    lateinit var apiOnError: ApiOnError

    private val vm: TimetableViewModel by activityViewModels()

    private val selectedPreviewTheme = BehaviorSubject.create<TimetableColorTheme>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSelectThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        vm.setPreviewTheme(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirm.throttledClicks()
            .flatMapCompletable {
                vm.updateTheme(tableDto.id, selectedPreviewTheme.value)
                    .doOnComplete {
                        vm.setPreviewTheme(null)
                        dismiss()
                    }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = apiOnError
            )

        selectedPreviewTheme
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                vm.setPreviewTheme(it)
                mapOf(
                    TimetableColorTheme.SNUTT to binding.snuttText,
                    TimetableColorTheme.MODERN to binding.modernText,
                    TimetableColorTheme.AUTUMN to binding.autumnText,
                    TimetableColorTheme.CHERRY to binding.pinkText,
                    TimetableColorTheme.ICE to binding.iceText,
                    TimetableColorTheme.JADE to binding.jadeText
                ).forEach { (theme, view) ->
                    view.background =
                        if (theme == it) requireContext().getDrawable(R.drawable.background_label)
                        else null
                }
            }

        mapOf(
            binding.snuttButton to TimetableColorTheme.SNUTT,
            binding.modernButton to TimetableColorTheme.MODERN,
            binding.autumnButton to TimetableColorTheme.AUTUMN,
            binding.pinkButton to TimetableColorTheme.CHERRY,
            binding.iceButton to TimetableColorTheme.ICE,
            binding.jadeButton to TimetableColorTheme.JADE
        ).forEach { (view, theme) ->
            view.setOnClickListener {
                selectedPreviewTheme.onNext(theme)
            }
        }
    }
}
