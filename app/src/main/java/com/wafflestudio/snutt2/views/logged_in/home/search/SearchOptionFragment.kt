package com.wafflestudio.snutt2.views.logged_in.home.search

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.DialogSearchOptionBinding
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.model.TagType
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy

@AndroidEntryPoint
class SearchOptionFragment : BottomSheetDialogFragment() {

    private val vm: SearchViewModel by activityViewModels()

    private lateinit var binding: DialogSearchOptionBinding

    private lateinit var adapter: TagSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typeMap: Map<TagType, TextView> = mapOf(
            TagType.ACADEMIC_YEAR to binding.academicYearButton,
            TagType.CATEGORY to binding.categoryButton,
            TagType.DEPARTMENT to binding.departmentButton,
            TagType.CREDIT to binding.creditButton,
            TagType.CLASSIFICATION to binding.classificationButton,
        )

        adapter = TagSelectionAdapter {
            vm.toggleTag(it)
        }

        binding.tagItems.adapter = this.adapter

        vm.tagsByTagType
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                adapter.submitList(it)
            }

        vm.selectedTagType
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                typeMap.forEach { (tagType, view) ->
                    view.setTextColor(
                        requireContext().getColor(
                            if (it == tagType) R.color.black
                            else R.color.gray2
                        )
                    )
                }
            }

        typeMap.forEach { (tagType, view) ->
            view.setOnClickListener {
                vm.setTagType(tagType)
            }
        }

        binding.filterButton.throttledClicks()
            .subscribeBy {
                vm.refreshQuery()
                dismiss()
            }
    }
}
