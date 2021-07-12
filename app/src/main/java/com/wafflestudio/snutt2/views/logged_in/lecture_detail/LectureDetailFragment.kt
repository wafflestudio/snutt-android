package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Preconditions
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.model.LectureItem
import com.wafflestudio.snutt2.ui.LectureMainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by makesource on 2016. 9. 4..
 */
@AndroidEntryPoint
class LectureDetailFragment : SNUTTBaseFragment() {

    @Inject
    lateinit var lectureManager: LectureManager

    @Inject
    lateinit var apiOnError: ApiOnError

    private var detailView: RecyclerView? = null

    private var lists: MutableList<LectureItem> = mutableListOf()

    private lateinit var adapter: LectureDetailAdapter

    var editable = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val lecture = lectureManager.currentLecture ?: return
        attachLectureDetailList(lecture)
        adapter = LectureDetailAdapter(
            this,
            lists,
            lectureManager,
            apiOnError,
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_lecture_detail_legacy, container, false)
        detailView = rootView.findViewById<View>(R.id.lecture_detail_view) as RecyclerView
        detailView!!.adapter = adapter
        detailView!!.layoutManager = LinearLayoutManager(context)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_lecture_detail, menu)
        val item = menu.getItem(0)
        if (editable) {
            item.title = "완료"
        } else {
            item.title = "편집"
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.getItem(0)
        if (editable) {
            item.title = "완료"
        } else {
            item.title = "편집"
        }
    }

    @Synchronized
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit ->
                if (editable) {
                    item.isEnabled = false
                    adapter.updateLecture(lectureManager.currentLecture)
                        .bindUi(
                            this,
                            onSuccess = {
                                item.title = "편집"
                                item.isEnabled = true
                                setNormalMode()
                            },
                            onError = {
                                item.isEnabled = true
                            }
                        )
                } else {
                    item.title = "완료"
                    setEditMode()
                }
            R.id.home -> if (editable) {
                refreshFragment()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
            for (item in lists) {
                if (item.type === LectureItem.Type.Color) return item
            }
            return null
        }

    fun refreshFragment() {
        editable = false
        hideSoftKeyboard(requireView())
        ActivityCompat.invalidateOptionsMenu(activity)
        lists.clear()
        attachLectureDetailList(lectureManager.currentLecture)
        adapter.notifyDataSetChanged()
    }

    private fun setNormalMode() {
        try {
            hideSoftKeyboard(requireView())
            editable = false
            for (i in lists.indices) {
                val it = lists[i]
                it.isEditable = false
                adapter.notifyItemChanged(i)
            }
            var pos = addClassTimeItemPosition
            lists.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            lists.add(pos, LectureItem(LectureItem.Type.Margin, false))
            adapter.notifyItemInserted(pos)
            lists.add(pos + 1, LectureItem(LectureItem.Type.LongHeader, false))
            adapter.notifyItemInserted(pos + 1)
            lists.add(pos + 2, LectureItem(LectureItem.Type.Syllabus, false))
            adapter.notifyItemInserted(pos + 2)

            // change button
            pos = resetItemPosition
            lists.removeAt(pos)
            lists.add(pos, LectureItem(LectureItem.Type.RemoveLecture, false))
            adapter.notifyItemChanged(pos)
        } catch (e: Exception) {
            Toast.makeText(app, "편집 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    private fun setEditMode() {
        try {
            editable = true
            for (i in lists.indices) {
                val it = lists[i]
                it.isEditable = true
                adapter.notifyItemChanged(i)
            }
            val syllabusPosition = syllabusItemPosition
            // remove syllabus
            lists.removeAt(syllabusPosition)
            adapter.notifyItemRemoved(syllabusPosition)
            // remove long header
            lists.removeAt(syllabusPosition - 1)
            adapter.notifyItemRemoved(syllabusPosition - 1)
            // remove margin
            lists.removeAt(syllabusPosition - 2)
            adapter.notifyItemRemoved(syllabusPosition - 2)
            val lastPosition = lastClassItemPosition
            // add button
            lists.add(lastPosition + 1, LectureItem(LectureItem.Type.AddClassTime, true))
            adapter.notifyItemInserted(lastPosition + 1)

            // change button
            val removePosition = removeItemPosition
            lists.removeAt(removePosition)
            lists.add(removePosition, LectureItem(LectureItem.Type.ResetLecture, true))
            adapter.notifyItemChanged(removePosition)
        } catch (e: Exception) {
            Toast.makeText(app, "편집 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    private fun attachLectureDetailList(lecture: LectureDto?) {
        with(lists) {
            add(LectureItem(LectureItem.Type.ShortHeader))
            add(LectureItem(LectureItem.Type.Margin))
            add(LectureItem("강의명", lecture!!.course_title, LectureItem.Type.Title))
            add(LectureItem("교수", lecture.instructor, LectureItem.Type.Instructor))
            add(
                LectureItem("색상", lecture.colorIndex.toInt(), lecture.color, LectureItem.Type.Color)
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
            add(LectureItem(LectureItem.Type.ShortHeader))
            add(LectureItem(LectureItem.Type.RemoveLecture))
            add(LectureItem(LectureItem.Type.LongHeader))
        }

    }

    private val syllabusItemPosition: Int
        get() {
            for (i in lists.indices) {
                if (lists[i].type === LectureItem.Type.Syllabus) return i
            }
            return -1
        }
    private val addClassTimeItemPosition: Int
        get() {
            for (i in lists.indices) {
                if (lists[i].type === LectureItem.Type.AddClassTime) return i
            }
            return -1
        }
    private val removeItemPosition: Int
        get() {
            for (i in lists.indices) {
                if (lists[i].type === LectureItem.Type.RemoveLecture) return i
            }
            return -1
        }
    private val resetItemPosition: Int
        get() {
            for (i in lists.indices) {
                if (lists[i].type === LectureItem.Type.ResetLecture) return i
            }
            return -1
        }
    private val classTimeHeaderPosition: Int
        get() {
            for (i in lists.indices) {
                if (lists[i].type === LectureItem.Type.ClassTimeHeader) return i
            }
            return -1
        }
    private val lastClassItemPosition: Int
        get() {
            for (i in classTimeHeaderPosition + 1 until lists.size) {
                if (lists[i].type !== LectureItem.Type.ClassTime) return i - 1
            }
            return lists.size - 1
        }
}
