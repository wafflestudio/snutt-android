package com.wafflestudio.snutt2.views.logged_in.home.search

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.ItemLectureSearchBinding
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
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
            binding.subTitle.text = lecture.instructor + " / " + lecture.credit + "학점"

            val tagText: String = listOf(
                lecture.category,
                lecture.department,
                lecture.academic_year
            )
                .filter { it.isNullOrBlank().not() }
                .let {
                    if (it.isEmpty()) "(없음)" else it.joinToString(", ")
                }

            val classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lecture).let {
                if (it.isBlank()) "(없음)" else it
            }
            val locationText = SNUTTStringUtils.getSimplifiedLocation(lecture).let {
                if (it.isBlank()) "(없음)" else it
            }

            if (data.state.selected) {
                binding.tag.text = lecture.remark
                binding.tag.ellipsize = TextUtils.TruncateAt.MARQUEE
                binding.content.setBackgroundColor(binding.root.context.getColor(R.color.black_a40))
            } else {
                binding.content.setBackgroundColor(binding.root.context.getColor(R.color.transparent))
            }

            binding.tag.let { tagView ->
                tagView.isSelected = data.state.selected
                tagView.text =
                    if (data.state.selected && lecture.remark.isNotBlank()) lecture.remark else tagText
                tagView.ellipsize =
                    if (data.state.selected) TextUtils.TruncateAt.MARQUEE else TextUtils.TruncateAt.END
            }
            binding.time.text = classTimeText
            binding.location.text = locationText
            binding.buttonGroup.isVisible = data.state.selected

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

