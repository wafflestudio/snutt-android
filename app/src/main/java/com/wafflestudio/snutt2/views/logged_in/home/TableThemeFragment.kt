package com.wafflestudio.snutt2.views.logged_in.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.databinding.DialogSelectThemeBinding
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@AndroidEntryPoint
class TableThemeFragment : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSelectThemeBinding

    private val vm: TimetableViewModel by activityViewModels()

    private val selectedTheme = BehaviorSubject.create<TimetableColorTheme>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSelectThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: NEED FIX
        selectedTheme.onNext(TimetableColorTheme.CUSTOM)

        binding.confirm.throttledClicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                vm.setColorTheme(selectedTheme.value)
                dismiss()
            }

        selectedTheme
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                mapOf(
                    TimetableColorTheme.CUSTOM to binding.customText,
                    TimetableColorTheme.SNUTT to binding.snuttText,
                    TimetableColorTheme.MODERN to binding.modernText,
                    TimetableColorTheme.AUTUMN to binding.autumnText,
                    TimetableColorTheme.PINK to binding.pinkText,
                    TimetableColorTheme.ICE to binding.iceText,
                    TimetableColorTheme.JADE to binding.jadeText
                ).forEach { (theme, view) ->
                    theme == it
                    view.background =
                        if (theme == it) requireContext().getDrawable(R.drawable.background_label)
                        else null
                }
            }

        mapOf(
            binding.customButton to TimetableColorTheme.CUSTOM,
            binding.snuttButton to TimetableColorTheme.SNUTT,
            binding.modernButton to TimetableColorTheme.MODERN,
            binding.autumnButton to TimetableColorTheme.AUTUMN,
            binding.pinkButton to TimetableColorTheme.PINK,
            binding.iceButton to TimetableColorTheme.ICE,
            binding.jadeButton to TimetableColorTheme.JADE
        ).forEach { (view, theme) ->
            view.setOnClickListener {
                selectedTheme.onNext(theme)
            }
        }
    }
}
