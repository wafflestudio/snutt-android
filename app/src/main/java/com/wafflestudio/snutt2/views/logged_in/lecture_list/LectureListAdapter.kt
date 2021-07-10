package com.wafflestudio.snutt2.views.logged_in.lecture_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Strings
import com.wafflestudio.snutt2.databinding.ItemAddLectureListBinding
import com.wafflestudio.snutt2.databinding.ItemLectureListBinding
import com.wafflestudio.snutt2.lib.network.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks

class LectureListAdapter(
    private val onClickAdd: () -> Unit,
    private val onClickLecture: (lecture: LectureDto) -> Unit
) :
    ListAdapter<LectureListAdapter.Data, LectureListAdapter.ViewHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> ViewHolder.DataViewHolder(
                ItemLectureListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            1 -> ViewHolder.AddButtonViewHolder(
                ItemAddLectureListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("no matching viewholder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Data.Lecture -> 0
            is Data.Add -> 1
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder.DataViewHolder -> {
                val lecture = (getItem(position) as Data.Lecture).lecture
                val binding = holder.binding

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
                if (Strings.isNullOrEmpty(tagText)) tagText = "(없음)"
                binding.tag.text = tagText
                var classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lecture)
                if (Strings.isNullOrEmpty(classTimeText)) classTimeText = "(없음)"
                binding.time.text = classTimeText
                var locationText = SNUTTStringUtils.getSimplifiedLocation(lecture)
                if (Strings.isNullOrEmpty(locationText)) locationText = "(없음)"
                binding.location.text = locationText
                binding.root.setOnClickListener {
                    onClickLecture.invoke(lecture)
                }
            }
            is ViewHolder.AddButtonViewHolder -> {
                val binding = holder.binding
                binding.root.setOnClickListener {
                    onClickAdd.invoke()
                }
            }
        }
    }


    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class DataViewHolder(val binding: ItemLectureListBinding) : ViewHolder(binding.root)

        class AddButtonViewHolder(val binding: ItemAddLectureListBinding) : ViewHolder(binding.root)
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
