package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.os.Bundle
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
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.databinding.FragmentLectureDetailBinding
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.hideSoftKeyboard
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.model.LectureItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomLectureDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentLectureDetailBinding

    @Inject
    lateinit var apiOnError: ApiOnError

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var myLectureRepository: MyLectureRepository

    private val vm: LectureDetailViewModel by viewModels()

    val args: CustomLectureDetailFragmentArgs by navArgs()

    private lateinit var detailView: RecyclerView
    private var adapter: CustomLectureAdapter? = null
    private var editable = false
    private var add = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        attachLectureDetailList(lecture)
        for (it in vm.lists) it.isEditable = add
        adapter =
            CustomLectureAdapter(
                this,
                vm.lists,
                myLectureRepository,
                apiOnError,
                lecture,
                {
                    childFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out
                        )
                        .add(R.id.color_select_fragment, LectureColorSelectorFragment())
                        .addToBackStack("color_picker")
                        .commit()
                },
                this
            )

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

        vm.selectedColor.asObservable()
            .distinctUntilChanged()
            .bindUi(this) {
                it.value?.let {
                    setLectureColor(it.first, it.second)
                }
            }

        binding.completeButton.throttledClicks()
            .bindUi(this) {
                when {
                    add -> {
                        adapter!!.createLecture()
                            .bindUi(
                                this,
                                onError = apiOnError,
                                onSuccess = {
                                    findNavController().popBackStack()
                                }
                            )
                    }
                    editable -> {
                        adapter!!.updateLecture()
                            .bindUi(
                                this,
                                onError = apiOnError,
                                onSuccess = {
                                    binding.completeButton.isVisible = false
                                    binding.editButton.isVisible = true
                                    setNormalMode()
                                }
                            )
                    }
                    else -> {
                        apiOnError
                    }
                }
            }

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
        hideSoftKeyboard()
        editable = false
        ActivityCompat.invalidateOptionsMenu(activity)
        vm.lists!!.clear()
        attachLectureDetailList(vm.selectedLecture.get().value)
        adapter!!.notifyDataSetChanged()
    }

    fun getEditable(): Boolean {
        return !add && editable
    }

    private fun attachLectureDetailList(lecture: LectureDto?) {
        vm.lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        vm.lists!!.add(LectureItem(LectureItem.Type.Margin))
        vm.lists!!.add(
            LectureItem("강의명", if (add) "" else lecture!!.course_title, LectureItem.Type.Title)
        )
        vm.lists!!.add(
            LectureItem("교수", if (add) "" else lecture!!.instructor, LectureItem.Type.Instructor)
        )
        vm.lists!!.add(
            LectureItem(
                "색상",
                if (add) 1 else lecture!!.colorIndex.toInt(),
                if (add) ColorDto() else lecture!!.color,
                vm.colorTheme,
                LectureItem.Type.Color
            )
        )
        vm.lists!!.add(
            LectureItem(
                "학점",
                if (add) "0" else lecture!!.credit.toString(),
                LectureItem.Type.Credit
            )
        )
        vm.lists!!.add(LectureItem(LectureItem.Type.Margin))
        vm.lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        vm.lists!!.add(LectureItem(LectureItem.Type.Margin))
        vm.lists!!.add(
            LectureItem(
                "비고",
                if (add) "" else lecture!!.remark,
                LectureItem.Type.Remark
            )
        )
        vm.lists!!.add(LectureItem(LectureItem.Type.Margin))
        vm.lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        vm.lists!!.add(LectureItem(LectureItem.Type.Margin))
        vm.lists!!.add(LectureItem(LectureItem.Type.ClassTimeHeader))
        if (!add) {
            for (classTime in lecture!!.class_time_json) {
                vm.lists!!.add(LectureItem(classTime, LectureItem.Type.ClassTime))
            }
            vm.lists!!.add(LectureItem(LectureItem.Type.Margin))
            vm.lists!!.add(LectureItem(LectureItem.Type.LongHeader))
            vm.lists!!.add(LectureItem(LectureItem.Type.RemoveLecture))
            vm.lists!!.add(LectureItem(LectureItem.Type.LongHeader))
        } else {
            vm.lists!!.add(LectureItem(LectureItem.Type.AddClassTime))
            vm.lists!!.add(LectureItem(LectureItem.Type.LongHeader))
        }
    }

    private fun setNormalMode() {
        hideSoftKeyboard()
        editable = false
        for (i in vm.lists!!.indices) {
            val it = vm.lists!![i]
            it.isEditable = false
            adapter!!.notifyItemChanged(i)
        }
        var pos = addClassTimeItemPosition
        vm.lists!!.removeAt(pos)
        adapter!!.notifyItemRemoved(pos)

        // add button & header
        pos = lastItem
        vm.lists!!.add(pos, LectureItem(LectureItem.Type.Margin, false))
        adapter!!.notifyItemInserted(pos)
        vm.lists!!.add(pos + 1, LectureItem(LectureItem.Type.LongHeader, false))
        adapter!!.notifyItemInserted(pos + 1)
        vm.lists!!.add(pos + 2, LectureItem(LectureItem.Type.RemoveLecture, false))
        adapter!!.notifyItemInserted(pos + 2)
    }

    private fun setEditMode() {
        editable = true
        for (i in vm.lists!!.indices) {
            val it = vm.lists!![i]
            it.isEditable = true
            adapter!!.notifyItemChanged(i)
        }
        val pos = removeItemPosition
        // remove button
        vm.lists!!.removeAt(pos)
        adapter!!.notifyItemRemoved(pos)
        // remove long header
        vm.lists!!.removeAt(pos - 1)
        adapter!!.notifyItemRemoved(pos - 1)
        // remove margin
        vm.lists!!.removeAt(pos - 2)
        adapter!!.notifyItemRemoved(pos - 2)
        val lastPosition = lastClassItemPosition
        // add button
        vm.lists!!.add(lastPosition + 1, LectureItem(LectureItem.Type.AddClassTime, true))
        adapter!!.notifyItemInserted(lastPosition + 1)
    }

    val colorItem: LectureItem?
        get() {
            for (item in vm.lists!!) {
                if (item.type === LectureItem.Type.Color) return item
            }
            return null
        }
    private val addClassTimeItemPosition: Int
        private get() {
            for (i in vm.lists!!.indices) {
                if (vm.lists!![i].type === LectureItem.Type.AddClassTime) return i
            }
            return -1
        }
    private val removeItemPosition: Int
        private get() {
            for (i in vm.lists!!.indices) {
                if (vm.lists!![i].type === LectureItem.Type.RemoveLecture) return i
            }
            return -1
        }
    private val classTimeHeaderPosition: Int
        private get() {
            for (i in vm.lists!!.indices) {
                if (vm.lists!![i].type === LectureItem.Type.ClassTimeHeader) return i
            }
            return -1
        }
    private val lastClassItemPosition: Int
        private get() {
            for (i in classTimeHeaderPosition + 1 until vm.lists!!.size) {
                if (vm.lists!![i].type !== LectureItem.Type.ClassTime) return i - 1
            }
            return vm.lists!!.size - 1
        }
    private val lastItem: Int
        private get() = vm.lists!!.size - 1

    companion object {
        private const val TAG = "CUSTOM_DETAIL_FRAGMENT"
    }
}
