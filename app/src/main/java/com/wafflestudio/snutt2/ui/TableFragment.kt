package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.view.*
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.manager.LectureManager.Companion.instance
import com.wafflestudio.snutt2.manager.LectureManager.OnLectureChangedListener
import com.wafflestudio.snutt2.view.TableView

/**
 * Created by makesource on 2016. 1. 16..
 */
class TableFragment : SNUTTBaseFragment(), OnLectureChangedListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_table, container, false)
        mInstance = rootView.findViewById<View>(R.id.timetable) as TableView
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_table, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_more) {
            mainActivity!!.startTableList()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        instance!!.removeListener(this)
    }

    override fun onResume() {
        super.onResume()
        instance!!.addListener(this)
    }

    override fun notifyLecturesChanged() {
        mInstance!!.invalidate()
    }

    override fun notifySearchedLecturesChanged() {}

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private var mInstance: TableView? = null

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): TableFragment {
            val fragment = TableFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}