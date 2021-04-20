package com.wafflestudio.snutt2.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.adapter.MyLectureListAdapter
import com.wafflestudio.snutt2.adapter.MyLectureListAdapter.LongClickListener
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.manager.LectureManager.OnLectureChangedListener
import com.wafflestudio.snutt2.network.dto.core.LectureDto
import com.wafflestudio.snutt2.view.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by makesource on 2016. 1. 16..
 */
@AndroidEntryPoint
class MyLectureFragment : SNUTTBaseFragment(), OnLectureChangedListener {

    @Inject
    lateinit var lectureManager: LectureManager

    @Inject
    lateinit var apiOnError: ApiOnError

    private var placeholder: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mAdapter: MyLectureListAdapter? = null
    private var lectures: List<LectureDto>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_lecture, container, false)
        setHasOptionsMenu(true)
        placeholder = rootView.findViewById<View>(R.id.placeholder) as LinearLayout
        recyclerView = rootView.findViewById<View>(R.id.my_lecture_recyclerView) as RecyclerView
        lectures = lectureManager.getLectures()
        mAdapter = MyLectureListAdapter(lectures)
        mAdapter!!.setOnItemClickListener(
            object : MyLectureListAdapter.ClickListener {
                override fun onClick(v: View?, position: Int) {
                    Log.d(TAG, "$position-th item clicked!")
                    mainActivity!!.startLectureMain(position)
                }
            }
        )
        mAdapter!!.setOnItemLongClickListener(
            object : LongClickListener {
                override fun onLongClick(v: View?, position: Int) {
                    Log.d(TAG, "$position-th item long clicked!")
                    val lecture = lectures!![position]
                    val items = arrayOf<CharSequence>(DIALOG_DETAIL, DIALOG_SYLLABUS, DIALOG_DELETE)
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle(lecture.course_title)
                        .setItems(items) { dialog, index ->
                            if (items[index] == DIALOG_DETAIL) {
                                mainActivity!!.startLectureMain(position)
                            } else if (items[index] == DIALOG_SYLLABUS) {
                                startSyllabus(lecture.course_number!!, lecture.lecture_number!!)
                            } else {
                                lectureManager.removeLecture(lecture.id)
                                    .bindUi(this@MyLectureFragment,
                                        onSuccess = {},
                                        onError = {
                                            apiOnError(it)
                                        })
                            }
                        }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        )
        recyclerView!!.addItemDecoration(
            DividerItemDecoration(requireContext(), R.drawable.lecture_divider)
        )
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = mAdapter
        placeholder!!.visibility = if (lectures!!.size == 0) View.VISIBLE else View.GONE
        return rootView
    }

    override fun notifyLecturesChanged() {
        Log.d(TAG, "notify lecture changed called")
        requireActivity().runOnUiThread {
            mAdapter!!.notifyDataSetChanged()
            placeholder!!.visibility = if (lectures!!.size == 0) View.VISIBLE else View.GONE
        }
    }

    override fun notifySearchedLecturesChanged() {}
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_my_lecture, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_add) {
            // getMainActivity().startTableList();
            // Toast.makeText(getContext(), "custom lecture add clicked!!", Toast.LENGTH_SHORT).show();
            mainActivity!!.startLectureMain()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        lectureManager.removeListener(this)
    }

    override fun onResume() {
        super.onResume()
        lectureManager.addListener(this)
        if (mAdapter != null) {
            // 강의 색상 변경시 fragment 이동 발생!
            mAdapter!!.notifyDataSetChanged()
        }
        placeholder!!.visibility = if (lectures!!.size == 0) View.VISIBLE else View.GONE
    }

    private fun startSyllabus(courseNumber: String, lectureNumber: String) {
        lectureManager.getCoursebookUrl(
            courseNumber,
            lectureNumber)
            .bindUi(this, onSuccess = { result ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                startActivity(intent)
            }, onError = {
                apiOnError(it)
            })

    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val TAG = "MY_LECTURE_FRAGMENT"
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val DIALOG_DETAIL = "상세보기"
        private const val DIALOG_SYLLABUS = "강의계획서"
        private const val DIALOG_DELETE = "삭제"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): MyLectureFragment {
            val fragment = MyLectureFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}
