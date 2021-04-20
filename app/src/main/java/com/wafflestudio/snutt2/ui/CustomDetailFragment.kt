package com.wafflestudio.snutt2.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Preconditions
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.adapter.CustomLectureAdapter
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.model.LectureItem
import com.wafflestudio.snutt2.network.dto.core.ColorDto
import com.wafflestudio.snutt2.network.dto.core.LectureDto
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

/**
 * Created by makesource on 2016. 11. 10..
 */
@AndroidEntryPoint
class CustomDetailFragment : SNUTTBaseFragment() {

    @Inject
    lateinit var lectureManager: LectureManager

    @Inject
    lateinit var apiOnError: ApiOnError

    private var detailView: RecyclerView? = null
    private var lists: ArrayList<LectureItem>? = null
    private var adapter: CustomLectureAdapter? = null
    private var editable = false
    private var add = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val lecture = lectureManager.currentLecture
        if (lecture == null) add = true
        lists = ArrayList()
        attachLectureDetailList(lecture)
        for (it in lists!!) it.isEditable = add
        adapter = CustomLectureAdapter(requireActivity(), lists!!, lectureManager, apiOnError, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_lecture_detail, container, false)
        detailView = rootView.findViewById<View>(R.id.lecture_detail_view) as RecyclerView
        detailView!!.adapter = adapter
        detailView!!.layoutManager = LinearLayoutManager(context)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_lecture_detail, menu)
        val item = menu.getItem(0)
        if (editable || add) {
            item.title = "완료"
        } else {
            item.title = "편집"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit ->
                if (add) {
                    item.isEnabled = false
                    adapter!!.createLecture()
                        .bindUi(this,
                            onSuccess = {
                                lectureMainActivity!!.finish()
                            },
                            onError = {
                                item.isEnabled = true
                                apiOnError(it)
                            }
                        )
                } else if (editable) {
                    item.isEnabled = false
                    adapter!!.updateLecture(lectureManager.currentLecture)
                        .bindUi(this,
                            onSuccess = {
                                item.title = "편집"
                                item.isEnabled = true
                                setNormalMode()
                            },
                            onError = {
                                item.isEnabled = true
                                apiOnError(it)
                            }
                        )
                } else {
                    item.title = "완료"
                    setEditMode()
                }
        }
        return super.onOptionsItemSelected(item)
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
        attachLectureDetailList(lectureManager.currentLecture)
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
                if (add) 0 else lecture!!.colorIndex.toInt(),
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
        hideSoftKeyboard(requireView())
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
    private val lectureMainActivity: LectureMainActivity?
        private get() {
            val activity: Activity? = activity
            Preconditions.checkArgument(activity is LectureMainActivity)
            return activity as LectureMainActivity?
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

        @JvmStatic
        fun newInstance(): CustomDetailFragment {
            return CustomDetailFragment()
        }
    }
}
