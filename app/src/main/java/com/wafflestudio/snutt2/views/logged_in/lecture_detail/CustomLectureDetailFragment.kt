package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.databinding.FragmentLectureDetailBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.model.LectureItem
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CustomLectureDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentLectureDetailBinding

    @Inject
    lateinit var apiOnError: ApiOnError

    @Inject
    lateinit var myLectureRepository: MyLectureRepository


    private val vm: LectureDetailViewModel by viewModels()

    val args: CustomLectureDetailFragmentArgs by navArgs()

    private lateinit var detailView: RecyclerView
    private var lists: ArrayList<LectureItem>? = null
    private var adapter: CustomLectureAdapter? = null
    private var editable = false
    private var add = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm.setLecture(args.selectedLecture)

        binding = FragmentLectureDetailBinding.inflate(inflater, container, false)
        detailView = binding.lectureDetailView

        val lecture = args.selectedLecture
        if (lecture == null) {
            binding.completeButton.isVisible = true
            binding.editButton.isVisible = false
            add = true
        } else {
            binding.completeButton.isVisible = false
            binding.editButton.isVisible = true
            vm.setLecture(lecture)
        }

        lists = ArrayList()
        attachLectureDetailList(lecture)
        for (it in lists!!) it.isEditable = add
        adapter =
            CustomLectureAdapter(this, lists!!, myLectureRepository, apiOnError, this)

        detailView.adapter = adapter
        detailView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }

        binding.completeButton.throttledClicks()
            .flatMapSingle {
                when {
                    add -> {
                        adapter!!.createLecture()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess { findNavController().popBackStack() }
                    }
                    editable -> {
                        adapter!!.updateLecture()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess {
                                binding.completeButton.isVisible = false
                                binding.editButton.isVisible = true
                                setNormalMode()
                            }
                    }
                    else -> {
                        Single.error(Error("illegal status"))
                    }
                }
            }
            .bindUi(this, onError = apiOnError)

        binding.editButton.throttledClicks()
            .bindUi(this) {
                binding.completeButton.isVisible = true
                binding.editButton.isVisible = false
                setEditMode()
            }

    }

    fun setLectureColor(index: Int, color: ColorDto?) {
        if (index > 0) {
            colorItem!!.colorIndex = index
        } else {
            colorItem!!.setColor(color) // 색상
            colorItem!!.colorIndex = 0
        }
        adapter!!.notifyDataSetChanged()
    }

    fun refreshFragment() {
        editable = false
        ActivityCompat.invalidateOptionsMenu(activity)
        lists!!.clear()
        attachLectureDetailList(vm.getSelectedLecture().get())
        adapter!!.notifyDataSetChanged()
    }

    fun getEditable(): Boolean {
        return !add && editable
    }

    private fun attachLectureDetailList(lecture: LectureDto?) {
        lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(
            LectureItem("강의명", if (add) "" else lecture!!.course_title, LectureItem.Type.Title)
        )
        lists!!.add(
            LectureItem("교수", if (add) "" else lecture!!.instructor, LectureItem.Type.Instructor)
        )
        lists!!.add(
            LectureItem(
                "색상",
                if (add) 1 else lecture!!.colorIndex.toInt(),
                if (add) ColorDto() else lecture!!.color,
                LectureItem.Type.Color
            )
        )
        lists!!.add(
            LectureItem(
                "학점",
                if (add) "0" else lecture!!.credit.toString(),
                LectureItem.Type.Credit
            )
        )
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem("비고", if (add) "" else lecture!!.remark, LectureItem.Type.Remark))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem(LectureItem.Type.ClassTimeHeader))
        if (!add) {
            for (classTime in lecture!!.class_time_json) {
                lists!!.add(LectureItem(classTime, LectureItem.Type.ClassTime))
            }
            lists!!.add(LectureItem(LectureItem.Type.Margin))
            lists!!.add(LectureItem(LectureItem.Type.LongHeader))
            lists!!.add(LectureItem(LectureItem.Type.RemoveLecture))
            lists!!.add(LectureItem(LectureItem.Type.LongHeader))
        } else {
            lists!!.add(LectureItem(LectureItem.Type.AddClassTime))
            lists!!.add(LectureItem(LectureItem.Type.LongHeader))
        }
    }

    private fun setNormalMode() {
        editable = false
        for (i in lists!!.indices) {
            val it = lists!![i]
            it.isEditable = false
            adapter!!.notifyItemChanged(i)
        }
        var pos = addClassTimeItemPosition
        lists!!.removeAt(pos)
        adapter!!.notifyItemRemoved(pos)

        // add button & header
        pos = lastItem
        lists!!.add(pos, LectureItem(LectureItem.Type.Margin, false))
        adapter!!.notifyItemInserted(pos)
        lists!!.add(pos + 1, LectureItem(LectureItem.Type.LongHeader, false))
        adapter!!.notifyItemInserted(pos + 1)
        lists!!.add(pos + 2, LectureItem(LectureItem.Type.RemoveLecture, false))
        adapter!!.notifyItemInserted(pos + 2)
    }

    private fun setEditMode() {
        editable = true
        for (i in lists!!.indices) {
            val it = lists!![i]
            it.isEditable = true
            adapter!!.notifyItemChanged(i)
        }
        val pos = removeItemPosition
        // remove button
        lists!!.removeAt(pos)
        adapter!!.notifyItemRemoved(pos)
        // remove long header
        lists!!.removeAt(pos - 1)
        adapter!!.notifyItemRemoved(pos - 1)
        // remove margin
        lists!!.removeAt(pos - 2)
        adapter!!.notifyItemRemoved(pos - 2)
        val lastPosition = lastClassItemPosition
        // add button
        lists!!.add(lastPosition + 1, LectureItem(LectureItem.Type.AddClassTime, true))
        adapter!!.notifyItemInserted(lastPosition + 1)
    }

    val colorItem: LectureItem?
        get() {
            for (item in lists!!) {
                if (item.type === LectureItem.Type.Color) return item
            }
            Log.e(TAG, "can't find color item")
            return null
        }
    private val addClassTimeItemPosition: Int
        private get() {
            for (i in lists!!.indices) {
                if (lists!![i].type === LectureItem.Type.AddClassTime) return i
            }
            Log.e(TAG, "can't find add class time item")
            return -1
        }
    private val removeItemPosition: Int
        private get() {
            for (i in lists!!.indices) {
                if (lists!![i].type === LectureItem.Type.RemoveLecture) return i
            }
            Log.e(TAG, "can't find syllabus item")
            return -1
        }
    private val classTimeHeaderPosition: Int
        private get() {
            for (i in lists!!.indices) {
                if (lists!![i].type === LectureItem.Type.ClassTimeHeader) return i
            }
            Log.e(TAG, "can't find class time header item")
            return -1
        }
    private val lastClassItemPosition: Int
        private get() {
            for (i in classTimeHeaderPosition + 1 until lists!!.size) {
                if (lists!![i].type !== LectureItem.Type.ClassTime) return i - 1
            }
            return lists!!.size - 1
        }
    private val lastItem: Int
        private get() = lists!!.size - 1

    companion object {
        private const val TAG = "CUSTOM_DETAIL_FRAGMENT"
    }
}
