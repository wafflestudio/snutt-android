package com.wafflestudio.snutt2.views.logged_in.lecture_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.wafflestudio.snutt2.databinding.ItemAddLectureListBinding
import com.wafflestudio.snutt2.databinding.ItemLectureListBinding
import com.wafflestudio.snutt2.lib.base.BaseAdapter
import com.wafflestudio.snutt2.lib.base.BaseViewHolder
import com.wafflestudio.snutt2.lib.network.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto

class LectureListAdapter(
    private val onClickAdd: () -> Unit,
    private val onClickLecture: (lecture: LectureDto) -> Unit
) : BaseAdapter<LectureListAdapter.Data>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Data.Lecture -> 0
            is Data.Add -> 1
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<out Data> {
        return when (viewType) {
            0 -> DataViewHolder(
                ItemLectureListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            1 -> AddButtonViewHolder(
                ItemAddLectureListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("no matching viewholder")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<out Data>, position: Int) {
        when (holder) {
            is DataViewHolder -> {
                holder.bindData(getItem(position) as Data.Lecture)
            }
            is AddButtonViewHolder -> {
                holder.bindData(getItem(position) as Data.Add)
            }
        }
    }


    inner class DataViewHolder(override val binding: ItemLectureListBinding) :
        BaseViewHolder<Data.Lecture>(binding) {

        override fun bindData(data: Data.Lecture) {
            val lecture = data.lecture

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
                onClickLecture.invoke(lecture)
            }
        }
    }


    inner class AddButtonViewHolder(override val binding: ItemAddLectureListBinding) :
        BaseViewHolder<Data.Add>(binding) {

        override fun bindData(data: Data.Add) {
            binding.root.setOnClickListener {
                onClickAdd.invoke()
            }
        }
    }

    sealed class Data {
        class Lecture(val lecture: LectureDto) : Data()

        object Add : Data()
    }


    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
                return when (oldItem) {
                    is Data.Lecture -> (newItem as? Data.Lecture)?.lecture?.id == oldItem.lecture.id
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem == newItem
            }
        }
    }
}
