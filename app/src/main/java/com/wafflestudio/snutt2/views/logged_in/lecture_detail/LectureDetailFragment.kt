package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentLectureDetailBinding
import com.wafflestudio.snutt2.lib.SnuttUrls
import com.wafflestudio.snutt2.lib.android.HomePage
import com.wafflestudio.snutt2.lib.android.HomePagerController
import com.wafflestudio.snutt2.lib.android.ReviewUrlController
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.android.defaultNavOptions
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.hideSoftKeyboard
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.model.LectureItem
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by makesource on 2016. 9. 4..
 */
// FIXME: 리팩토링이 많이 필요할 듯, UI 변경이 된다면 그 떄 같이 건드리면 좋을 것 같음
@AndroidEntryPoint
class LectureDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentLectureDetailBinding

    val args: LectureDetailFragmentArgs by navArgs()

    @Inject
    lateinit var apiOnError: ApiOnError

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var snuttUrls: SnuttUrls

    @Inject
    lateinit var homePagerController: HomePagerController

    @Inject
    lateinit var reviewUrlController: ReviewUrlController

    @Inject
    lateinit var apiService: SNUTTRestApi

    private lateinit var adapter: LectureDetailAdapter

    private val vm: LectureDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLectureDetailBinding.inflate(inflater, container, false)

        val selectedLecture = args.selectedLecture

        vm.setLecture(selectedLecture)
        vm.lists.clear()
        attachLectureDetailList(selectedLecture)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (vm.isEditMode.get())
                dialogController.showConfirm(message = R.string.lecture_detail_cancel_editing)
                    .bindUi(
                        this@LectureDetailFragment,
                        onSuccess = {
                            findNavController().popBackStack()
                        }
                    )
            else {
                findNavController().popBackStack()
            }
        }

        adapter = LectureDetailAdapter(
            vm.lists,
            onSyllabus = { startSyllabus() },
            onRemoveLecture = { startRemoveAlertView() },
            onLectureReview = { openLectureReview() },
            onResetLecture = { startResetAlertView() },
            onChangeColor = {
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
            }
        )

        binding.lectureDetailView.adapter = adapter
        binding.lectureDetailView.layoutManager = LinearLayoutManager(context)

        vm.isEditMode.asObservable()
            .distinctUntilChanged()
            .bindUi(this) {
                binding.completeButton.isVisible = it
                binding.editButton.isVisible = false // 서버 강의는 편집 불가능
            }

        vm.selectedColor.asObservable()
            .distinctUntilChanged()
            .bindUi(this) {
                it.value?.let {
                    setLectureColor(it.first, it.second)
                }
            }

        binding.completeButton.throttledClicks()
            .flatMapSingle {
                vm.updateLecture(adapter.getUpdateParam())
            }
            .bindUi(
                this,
                onNext = {
                    setNormalMode()
                },
                onError = apiOnError
            )

        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }
    }

    fun setLectureColor(index: Int, color: ColorDto?) {
        if (index > 0) {
            colorItem!!.colorIndex = index
        } else {
            colorItem!!.setColor(color) // 색상
        }
        adapter.notifyDataSetChanged()
    }

    val colorItem: LectureItem?
        get() {
            for (item in vm.lists) {
                if (item.type === LectureItem.Type.Color) return item
            }
            return null
        }

    fun refreshFragment(find: LectureDto?) {
        vm.setEditMode(false)
        vm.setLecture(find)
        vm.lists.clear()
        attachLectureDetailList(find)
        adapter.notifyDataSetChanged()
    }

    private fun setNormalMode() {
        try {
            vm.setEditMode(false)
            hideSoftKeyboard()
            for (i in vm.lists.indices) {
                val it = vm.lists[i]
                it.isEditable = false
                adapter.notifyItemChanged(i)
            }
            var pos = addClassTimeItemPosition
            vm.lists.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            vm.lists.add(pos, LectureItem(LectureItem.Type.Margin, false))
            adapter.notifyItemInserted(pos)
            vm.lists.add(pos + 1, LectureItem(LectureItem.Type.LongHeader, false))
            adapter.notifyItemInserted(pos + 1)
            vm.lists.add(pos + 2, LectureItem(LectureItem.Type.Syllabus, false))
            adapter.notifyItemInserted(pos + 2)
            vm.lists.add(pos + 3, LectureItem(LectureItem.Type.LectureReview, false))
            adapter.notifyItemInserted(pos + 3)

            // change button
            pos = resetItemPosition
            vm.lists.removeAt(pos)
            vm.lists.add(pos, LectureItem(LectureItem.Type.RemoveLecture, false))
            adapter.notifyItemChanged(pos)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "편집 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    private fun setEditMode() {
        try {
            vm.setEditMode(true)
            for (i in vm.lists.indices) {
                val it = vm.lists[i]
                it.isEditable = true
                adapter.notifyItemChanged(i)
            }
            val syllabusPosition = syllabusItemPosition
            // remove lecture review
            vm.lists.removeAt(syllabusPosition + 1)
            adapter.notifyItemRemoved(syllabusPosition + 1)
            // remove syllabus
            vm.lists.removeAt(syllabusPosition)
            adapter.notifyItemRemoved(syllabusPosition)
            // remove long header
            vm.lists.removeAt(syllabusPosition - 1)
            adapter.notifyItemRemoved(syllabusPosition - 1)
            // remove margin
            vm.lists.removeAt(syllabusPosition - 2)
            adapter.notifyItemRemoved(syllabusPosition - 2)
            val lastPosition = lastClassItemPosition
            // add button
            vm.lists.add(lastPosition + 1, LectureItem(LectureItem.Type.AddClassTime, true))
            adapter.notifyItemInserted(lastPosition + 1)

            // change button
            val removePosition = removeItemPosition
            vm.lists.removeAt(removePosition)
            vm.lists.add(removePosition, LectureItem(LectureItem.Type.ResetLecture, true))
            adapter.notifyItemChanged(removePosition)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "편집 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    private fun attachLectureDetailList(lecture: LectureDto?) {
        with(vm.lists) {
            add(LectureItem(LectureItem.Type.ShortHeader))
            add(LectureItem(LectureItem.Type.Margin))
            add(LectureItem("강의명", lecture!!.course_title, LectureItem.Type.Title))
            add(LectureItem("교수", lecture.instructor, LectureItem.Type.Instructor))
            add(
                LectureItem(
                    "색상",
                    lecture.colorIndex.toInt(),
                    lecture.color,
                    vm.colorTheme,
                    LectureItem.Type.Color
                )
            )
            add(LectureItem(LectureItem.Type.Margin))
            add(LectureItem(LectureItem.Type.ShortHeader))
            add(LectureItem(LectureItem.Type.Margin))
            add(LectureItem("학과", lecture.department, LectureItem.Type.Department))
            add(LectureItem("학년", lecture.academic_year, LectureItem.Type.AcademicYear))
            add(LectureItem("학점", lecture.credit.toString(), LectureItem.Type.Credit))
            add(LectureItem("분류", lecture.classification, LectureItem.Type.Classification))
            add(LectureItem("구분", lecture.category, LectureItem.Type.Category))
            add(LectureItem("강좌번호", lecture.course_number, LectureItem.Type.CourseNumber))
            add(LectureItem("분반번호", lecture.lecture_number, LectureItem.Type.LectureNumber))
            add(LectureItem("비고", lecture.remark, LectureItem.Type.Remark))
            add(LectureItem(LectureItem.Type.Margin))
            add(LectureItem(LectureItem.Type.ShortHeader))
            add(LectureItem(LectureItem.Type.Margin))
            add(LectureItem(LectureItem.Type.ClassTimeHeader))
            for (classTime in lecture.class_time_json) {
                add(LectureItem(classTime, LectureItem.Type.ClassTime))
            }
            add(LectureItem(LectureItem.Type.Margin))
            add(LectureItem(LectureItem.Type.LongHeader))
            add(LectureItem(LectureItem.Type.Syllabus))
            add(LectureItem(LectureItem.Type.LectureReview))
            add(LectureItem(LectureItem.Type.ShortHeader))
            add(LectureItem(LectureItem.Type.RemoveLecture))
            add(LectureItem(LectureItem.Type.LongHeader))
        }
    }

    private fun startSyllabus() {
        vm.getCourseBookUrl()
            .bindUi(
                this,
                onSuccess = { result ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                    requireContext().startActivity(intent)
                },
                onError = apiOnError
            )
    }

    private fun startRemoveAlertView() {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("강좌 삭제")
            .setMessage("강좌를 삭제하시겠습니까")
            .setPositiveButton("삭제") { _, _ ->
                vm
                    .removeLecture()
                    .bindUi(
                        this,
                        onComplete = {
                            findNavController().popBackStack()
                        },
                        onError = apiOnError
                    )
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun openLectureReview() {
        vm.getReviewContentsUrl()
            .bindUi(
                this,
                onSuccess = {
                    findNavController().popBackStack()
                    // pop back 이후 딜레이를 주지 않고 강의평으로 보내버리면 가끔 스크롤링이 무시되어 임시방편 처리함
                    Completable.timer(150, TimeUnit.MILLISECONDS)
                        .subscribe {
                            homePagerController.update(HomePage.Review)
                            reviewUrlController.update(it)
                        }
                }, onError = apiOnError
            )
    }

    private fun routeColorSelector() {
        findNavController().navigate(
            LectureDetailFragmentDirections.actionLectureDetailFragmentToLectureColorSelectorFragment(),
            defaultNavOptions
        )
    }

    private fun startResetAlertView() {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("강좌 초기화")
            .setMessage("강좌를 원래 상태로 초기화하시겠습니까")
            .setPositiveButton(
                "초기화"
            ) { _, _ ->
                vm
                    .resetLecture()
                    .bindUi(
                        this,
                        onSuccess = {
                            refreshFragment(it.lectureList.find { it.id == args.selectedLecture.id })
                        },
                        onError = apiOnError
                    )
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private val syllabusItemPosition: Int
        get() {
            for (i in vm.lists.indices) {
                if (vm.lists[i].type === LectureItem.Type.Syllabus) return i
            }
            return -1
        }
    private val addClassTimeItemPosition: Int
        get() {
            for (i in vm.lists.indices) {
                if (vm.lists[i].type === LectureItem.Type.AddClassTime) return i
            }
            return -1
        }
    private val removeItemPosition: Int
        get() {
            for (i in vm.lists.indices) {
                if (vm.lists[i].type === LectureItem.Type.RemoveLecture) return i
            }
            return -1
        }
    private val resetItemPosition: Int
        get() {
            for (i in vm.lists.indices) {
                if (vm.lists[i].type === LectureItem.Type.ResetLecture) return i
            }
            return -1
        }
    private val classTimeHeaderPosition: Int
        get() {
            for (i in vm.lists.indices) {
                if (vm.lists[i].type === LectureItem.Type.ClassTimeHeader) return i
            }
            return -1
        }
    private val lastClassItemPosition: Int
        get() {
            for (i in classTimeHeaderPosition + 1 until vm.lists.size) {
                if (vm.lists[i].type !== LectureItem.Type.ClassTime) return i - 1
            }
            return vm.lists.size - 1
        }
}
