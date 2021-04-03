package com.wafflestudio.snutt2.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Preconditions
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.adapter.LectureDetailAdapter
import com.wafflestudio.snutt2.manager.LectureManager.Companion.instance
import com.wafflestudio.snutt2.model.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.lang.String
import java.util.*

/**
 * Created by makesource on 2016. 9. 4..
 */
class LectureDetailFragment : SNUTTBaseFragment() {
    private var detailView: RecyclerView? = null
    private var lists: ArrayList<LectureItem>? = null
    private var adapter: LectureDetailAdapter? = null
    var editable = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val lecture = instance!!.currentLecture
        if (lecture == null) {
            Log.e(TAG, "lecture refers to null point!!")
            return
        }
        lists = ArrayList()
        attachLectureDetailList(lecture)
        adapter = LectureDetailAdapter(lectureMainActivity!!, this, lists!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_lecture_detail, container, false)
        detailView = rootView.findViewById<View>(R.id.lecture_detail_view) as RecyclerView
        detailView!!.adapter = adapter
        detailView!!.layoutManager = LinearLayoutManager(context)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(TAG, "on create options menu called")
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
        Log.d(TAG, "on prepare options menu called")
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
            R.id.action_edit -> if (editable) {
                item.isEnabled = false
                adapter!!.updateLecture(instance!!.currentLecture, object : Callback<Table?> {
                    override fun success(table: Table?, response: Response) {
                        item.title = "편집"
                        item.isEnabled = true
                        setNormalMode()
                    }

                    override fun failure(error: RetrofitError) {
                        item.isEnabled = true
                    }
                })
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

    fun setLectureColor(index: Int, color: Color?) {
        if (index > 0) {
            colorItem!!.colorIndex = index
        } else {
            colorItem!!.setColor(color) // 색상
        }
        adapter!!.notifyDataSetChanged()
    }

    val colorItem: LectureItem?
        get() {
            for (item in lists!!) {
                if (item.type === LectureItem.Type.Color) return item
            }
            Log.e(TAG, "can't find color item")
            return null
        }

    fun refreshFragment() {
        editable = false
        hideSoftKeyboard(view!!)
        ActivityCompat.invalidateOptionsMenu(activity)
        lists!!.clear()
        attachLectureDetailList(instance!!.currentLecture)
        adapter!!.notifyDataSetChanged()
    }

    private val lectureMainActivity: LectureMainActivity?
        private get() {
            val activity: Activity? = activity
            Preconditions.checkArgument(activity is LectureMainActivity)
            return activity as LectureMainActivity?
        }

    private fun setNormalMode() {
        try {
            hideSoftKeyboard(view!!)
            editable = false
            for (i in lists!!.indices) {
                val it = lists!![i]
                it.isEditable = false
                adapter!!.notifyItemChanged(i)
            }
            var pos = addClassTimeItemPosition
            lists!!.removeAt(pos)
            adapter!!.notifyItemRemoved(pos)
            lists!!.add(pos, LectureItem(LectureItem.Type.Margin, false))
            adapter!!.notifyItemInserted(pos)
            lists!!.add(pos + 1, LectureItem(LectureItem.Type.LongHeader, false))
            adapter!!.notifyItemInserted(pos + 1)
            lists!!.add(pos + 2, LectureItem(LectureItem.Type.Syllabus, false))
            adapter!!.notifyItemInserted(pos + 2)

            // change button
            pos = resetItemPosition
            lists!!.removeAt(pos)
            lists!!.add(pos, LectureItem(LectureItem.Type.RemoveLecture, false))
            adapter!!.notifyItemChanged(pos)
        } catch (e: Exception) {
            Toast.makeText(app, "편집 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            activity!!.finish()
        }
    }

    private fun setEditMode() {
        try {
            editable = true
            for (i in lists!!.indices) {
                val it = lists!![i]
                it.isEditable = true
                adapter!!.notifyItemChanged(i)
            }
            val syllabusPosition = syllabusItemPosition
            // remove syllabus
            lists!!.removeAt(syllabusPosition)
            adapter!!.notifyItemRemoved(syllabusPosition)
            // remove long header
            lists!!.removeAt(syllabusPosition - 1)
            adapter!!.notifyItemRemoved(syllabusPosition - 1)
            // remove margin
            lists!!.removeAt(syllabusPosition - 2)
            adapter!!.notifyItemRemoved(syllabusPosition - 2)
            val lastPosition = lastClassItemPosition
            // add button
            lists!!.add(lastPosition + 1, LectureItem(LectureItem.Type.AddClassTime, true))
            adapter!!.notifyItemInserted(lastPosition + 1)

            // change button
            val removePosition = removeItemPosition
            lists!!.removeAt(removePosition)
            lists!!.add(removePosition, LectureItem(LectureItem.Type.ResetLecture, true))
            adapter!!.notifyItemChanged(removePosition)
        } catch (e: Exception) {
            Toast.makeText(app, "편집 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            activity!!.finish()
        }
    }

    private fun attachLectureDetailList(lecture: Lecture?) {
        lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem("강의명", lecture!!.course_title, LectureItem.Type.Title))
        lists!!.add(LectureItem("교수", lecture.instructor, LectureItem.Type.Instructor))
        lists!!.add(LectureItem("색상", lecture.colorIndex, lecture.getColor(), LectureItem.Type.Color))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem("학과", lecture.department, LectureItem.Type.Department))
        lists!!.add(LectureItem("학년", lecture.academic_year, LectureItem.Type.AcademicYear))
        lists!!.add(LectureItem("학점", String.valueOf(lecture.credit), LectureItem.Type.Credit))
        lists!!.add(LectureItem("분류", lecture.classification, LectureItem.Type.Classification))
        lists!!.add(LectureItem("구분", lecture.category, LectureItem.Type.Category))
        lists!!.add(LectureItem("강좌번호", lecture.course_number, LectureItem.Type.CourseNumber))
        lists!!.add(LectureItem("분반번호", lecture.lecture_number, LectureItem.Type.LectureNumber))
        lists!!.add(LectureItem("비고", lecture.remark, LectureItem.Type.Remark))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem(LectureItem.Type.ClassTimeHeader))
        for (element in lecture.class_time_json!!) {
            val jsonObject = element.asJsonObject
            val classTime = ClassTime(jsonObject)
            lists!!.add(LectureItem(classTime, LectureItem.Type.ClassTime))
        }
        lists!!.add(LectureItem(LectureItem.Type.Margin))
        lists!!.add(LectureItem(LectureItem.Type.LongHeader))
        lists!!.add(LectureItem(LectureItem.Type.Syllabus))
        lists!!.add(LectureItem(LectureItem.Type.ShortHeader))
        lists!!.add(LectureItem(LectureItem.Type.RemoveLecture))
        lists!!.add(LectureItem(LectureItem.Type.LongHeader))
    }

    private val syllabusItemPosition: Int
        private get() {
            for (i in lists!!.indices) {
                if (lists!![i].type === LectureItem.Type.Syllabus) return i
            }
            Log.e(TAG, "can't find syllabus item")
            return -1
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
    private val resetItemPosition: Int
        private get() {
            for (i in lists!!.indices) {
                if (lists!![i].type === LectureItem.Type.ResetLecture) return i
            }
            Log.e(TAG, "can't find reset item")
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

    companion object {
        private const val TAG = "LECTURE_DETAIL_FRAGMENT"
        @JvmStatic
        fun newInstance(): LectureDetailFragment {
            return LectureDetailFragment()
        }
    }
}