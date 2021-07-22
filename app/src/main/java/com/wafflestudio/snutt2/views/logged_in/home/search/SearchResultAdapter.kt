package com.wafflestudio.snutt2.views.logged_in.home.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.ItemLectureSearchBinding
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.network.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto

class SearchResultAdapter(
    private val onSelectLecture: (LectureDto) -> Unit,
    private val onToggleAddition: (LectureDto) -> Unit,
    private val onShowSyllabus: (LectureDto) -> Unit,
    private val onShowReviews: (LectureDto) -> Unit,
) : PagingDataAdapter<DataWithState<LectureDto, LectureState>, SearchResultAdapter.ViewHolder>(
    diffCallback
) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bindData(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLectureSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: ItemLectureSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: DataWithState<LectureDto, LectureState>) {
            val lecture = data.item

            binding.title.text = lecture.course_title
            binding.subTitle.text = lecture.instructor + " / " + lecture.credit

            var tagText: String? = ""
            lecture.category?.let {
                tagText += "$it, "
            }
            lecture.department?.let {
                tagText += "$it, "
            }
            lecture.academic_year?.let {
                tagText += "$it, "
            }
            if (tagText.isNullOrEmpty()) tagText = "(없음)"
            binding.tag.text = tagText
            var classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lecture)
            if (classTimeText.isNullOrEmpty()) classTimeText = "(없음)"
            binding.time.text = classTimeText
            var locationText = SNUTTStringUtils.getSimplifiedLocation(lecture)
            if (locationText.isNullOrEmpty()) locationText = "(없음)"
            binding.location.text = locationText

            binding.root.setOnClickListener {
                onSelectLecture.invoke(lecture)
            }

            binding.toggleAdditionButton.setOnClickListener {
                onToggleAddition.invoke(lecture)
            }

            binding.syllabusButton.setOnClickListener {
                onShowSyllabus.invoke(lecture)
            }

            binding.reviewsButton.setOnClickListener {
                onShowReviews.invoke(lecture)
            }

            binding.buttonGroup.isVisible = data.state.selected

            binding.toggleAdditionButton.text = binding.root.context.getString(
                if (data.state.contained) R.string.search_result_item_remove_button
                else R.string.search_result_item_add_button
            )
        }
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<DataWithState<LectureDto, LectureState>>() {
                override fun areItemsTheSame(
                    oldItem: DataWithState<LectureDto, LectureState>,
                    newItem: DataWithState<LectureDto, LectureState>
                ): Boolean {
                    return oldItem.item.id == newItem.item.id
                }

                override fun areContentsTheSame(
                    oldItem: DataWithState<LectureDto, LectureState>,
                    newItem: DataWithState<LectureDto, LectureState>
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

data class LectureState(
    val selected: Boolean,
    val contained: Boolean
)
