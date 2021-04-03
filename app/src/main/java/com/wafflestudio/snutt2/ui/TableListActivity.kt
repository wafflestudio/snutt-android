package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.common.base.Strings
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.adapter.ExpandableTableListAdapter
import com.wafflestudio.snutt2.manager.PrefManager
import com.wafflestudio.snutt2.manager.TableManager.Companion.instance
import com.wafflestudio.snutt2.model.Coursebook
import com.wafflestudio.snutt2.model.Table
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*

/**
 * Created by makesource on 2016. 1. 17..
 */
class TableListActivity : SNUTTBaseActivity() {
    private var mGroupList: ArrayList<String>? = null
    private var mChildList: ArrayList<ArrayList<Table>>? = null
    private var placeholder: LinearLayout? = null
    private var coursebookList: List<Coursebook>? = null
    private var mListView: ExpandableListView? = null
    private var mAdapter: ExpandableTableListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityList.add(this)
        setContentView(R.layout.activity_table_list)
        title = "시간표 목록"
        placeholder = findViewById<View>(R.id.placeholder) as LinearLayout
        mListView = findViewById<View>(R.id.listView) as ExpandableListView
        mListView!!.setOnChildClickListener(
            OnChildClickListener { parent, v, groupPosition, childPosition, id ->
                if (mChildList!![groupPosition].isEmpty()) {
                    val coursebook = coursebookList!![groupPosition]
                    instance!!.postTable(
                        coursebook.year,
                        coursebook.semester,
                        "내 시간표",
                        object : Callback<List<Table>> {
                            override fun success(tables: List<Table>, response: Response) {
                                mAdapter = getAdapter(tables)
                                mListView!!.setAdapter(mAdapter)
                                for (i in mGroupList!!.indices) {
                                    mListView!!.expandGroup(i)
                                }
                            }

                            override fun failure(error: RetrofitError) {}
                        }
                    )
                    return@OnChildClickListener true
                }
                val tableId = mChildList!![groupPosition][childPosition].id
                startTableView(tableId)
                finish()
                true
            }
        )
        mListView!!.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                val groupPosition = ExpandableListView.getPackedPositionGroup(id)
                val childPosition = ExpandableListView.getPackedPositionChild(id)
                Log.d(TAG, "$groupPosition $childPosition")
                val table = mChildList!![groupPosition][childPosition]
                val items = arrayOf<CharSequence>(DIALOG_EDIT, DIALOG_DELETE)
                val builder = AlertDialog.Builder(this@TableListActivity)
                builder.setTitle(table.title)
                    .setItems(items) { dialog, index ->
                        if (items[index] == DIALOG_EDIT) showEditDialog(
                            table
                        ) else performDelete(table)
                    }
                val dialog = builder.create()
                dialog.show()
                // You now have everything that you would as if this was an OnChildClickListener()
                // Add your logic here.

                // Return true as we are handling the event.
                return@OnItemLongClickListener true
            }
            false
        }
        tableList
    }

    private val tableList: Unit
        private get() {
            instance!!.getCoursebook(
                object : Callback<List<Coursebook>> {
                    override fun success(coursebooks: List<Coursebook>?, response: Response) {
                        coursebookList = coursebooks
                        instance!!.getTableList(
                            object : Callback<List<Table>> {
                                override fun success(tables: List<Table>, response: Response) {
                                    mAdapter = getAdapter(tables)
                                    mListView!!.setAdapter(mAdapter)
                                    for (i in mGroupList!!.indices) {
                                        mListView!!.expandGroup(i)
                                    }
                                    placeholder!!.visibility = if (instance!!.hasTimetables()) View.GONE else View.VISIBLE
                                }

                                override fun failure(error: RetrofitError) {}
                            }
                        )
                    }

                    override fun failure(error: RetrofitError) {}
                }
            )
        }

    private fun getFullSemester(year: Int, semester: Int): String {
        val yearString: String
        val semesterString: String
        yearString = year.toString()
        when (semester) {
            1 -> semesterString = "1"
            2 -> semesterString = "S"
            3 -> semesterString = "2"
            4 -> semesterString = "W"
            else -> {
                semesterString = ""
                Log.e(TAG, "semester is out of range!!")
            }
        }
        return "$yearString-$semesterString"
    }

    private fun showEditDialog(table: Table) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.dialog_change_title, null)
        val alert = AlertDialog.Builder(this)
        alert.setTitle("시간표 이름 변경")
        alert.setView(layout)
        alert.setPositiveButton("변경") { dialog, whichButton ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton(
            "취소"
        ) { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = (layout.findViewById<View>(R.id.title) as EditText).text.toString()
            if (!Strings.isNullOrEmpty(title)) {
                instance!!.putTable(
                    table.id,
                    title,
                    object : Callback<List<Table>> {
                        override fun success(
                            tables: List<Table>,
                            response: Response
                        ) {
                            mAdapter = getAdapter(tables)
                            mListView!!.setAdapter(mAdapter)
                            for (i in mGroupList!!.indices) {
                                mListView!!.expandGroup(i)
                            }
                            placeholder!!.visibility = if (instance!!.hasTimetables()) View.GONE else View.VISIBLE
                        }

                        override fun failure(error: RetrofitError) {}
                    }
                )
                dialog.dismiss()
            } else {
                Toast.makeText(app, "시간표 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performDelete(table: Table) {
        if (PrefManager.instance!!.lastViewTableId.equals(table.id)) {
            Toast.makeText(app, "현재 보고있는 테이블은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        instance!!.deleteTable(
            table.id,
            object : Callback<List<Table>> {
                override fun success(tables: List<Table>, response: Response) {
                    mAdapter = getAdapter(tables)
                    mListView!!.setAdapter(mAdapter)
                    for (i in mGroupList!!.indices) {
                        mListView!!.expandGroup(i)
                    }
                    placeholder!!.visibility = if (instance!!.hasTimetables()) View.GONE else View.VISIBLE
                }

                override fun failure(error: RetrofitError) {}
            }
        )
    }

    private fun getAdapter(tables: List<Table>): ExpandableTableListAdapter {
        mGroupList = ArrayList()
        mChildList = ArrayList()
        for (coursebook in coursebookList!!) {
            mGroupList!!.add(getFullSemester(coursebook.year, coursebook.semester))
            mChildList!!.add(ArrayList())
        }
        var index = 0
        for (table in tables) {
            while (index < mGroupList!!.size && mGroupList!![index] != table.fullSemester) index++
            if (index >= mGroupList!!.size) break
            mChildList!![index].add(table)
        }
        return ExpandableTableListAdapter(this, mGroupList, mChildList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_table_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_add) {
            startTableCreate()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        tableList
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
    }

    companion object {
        private const val TAG = "TABLE_LIST_ACTIVITY"
        private const val DIALOG_EDIT = "시간표 이름 변경"
        private const val DIALOG_DELETE = "시간표 삭제"
    }
}
