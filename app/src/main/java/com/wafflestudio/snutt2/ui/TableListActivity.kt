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
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.manager.PrefStorage
import com.wafflestudio.snutt2.manager.TableManager
import com.wafflestudio.snutt2.model.Coursebook
import com.wafflestudio.snutt2.lib.network.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

/**
 * Created by makesource on 2016. 1. 17..
 */
@AndroidEntryPoint
class TableListActivity : SNUTTBaseActivity() {

    @Inject
    lateinit var tableManager: TableManager

    @Inject
    lateinit var prefStorage: PrefStorage

    @Inject
    lateinit var apiOnError: ApiOnError

    private var mGroupList: ArrayList<String>? = null
    private var mChildList: ArrayList<ArrayList<TableDto>>? = null
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
                    // Refactoring FIXME: error handling
                    tableManager.postTable(
                        coursebook.year,
                        coursebook.semester,
                        "내 시간표"
                    )
                        .bindUi(this@TableListActivity) {
                            mAdapter = getAdapter(it)
                            mListView!!.setAdapter(mAdapter)
                            for (i in mGroupList!!.indices) {
                                mListView!!.expandGroup(i)
                            }
                        }
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

    // Refactoring FIXME: error handle
    private val tableList: Unit
        get() {
            tableManager.getCoursebook()
                .map { it ->
                    it.map { item ->
                        Coursebook(item.semester, item.year)
                    }
                }
                .bindUi(this) {
                    coursebookList = it
                    tableManager.getTableList()
                        .bindUi(this@TableListActivity) {
                            mAdapter = getAdapter(it)
                            mListView!!.setAdapter(mAdapter)
                            for (i in mGroupList!!.indices) {
                                mListView!!.expandGroup(i)
                            }
                            placeholder!!.visibility = if (tableManager.hasTimetables()) View.GONE else View.VISIBLE
                        }
                }
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

    private fun showEditDialog(table: TableDto) {
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
                tableManager.putTable(table.id, title)
                    .bindUi(
                        this,
                        onSuccess = { tables ->
                            mAdapter = getAdapter(tables)
                            mListView!!.setAdapter(mAdapter)
                            for (i in mGroupList!!.indices) {
                                mListView!!.expandGroup(i)
                            }
                            placeholder!!.visibility = if (tableManager.hasTimetables()) View.GONE else View.VISIBLE
                        },
                        onError = {
                            apiOnError(it)
                        }
                    )
                dialog.dismiss()
            } else {
                Toast.makeText(app, "시간표 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performDelete(table: TableDto) {
        if (prefStorage.lastViewTableId.equals(table.id)) {
            Toast.makeText(app, "현재 보고있는 테이블은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        tableManager.deleteTable(table.id)
            .bindUi(
                this,
                onSuccess = { tables ->
                    mAdapter = getAdapter(tables)
                    mListView!!.setAdapter(mAdapter)
                    for (i in mGroupList!!.indices) {
                        mListView!!.expandGroup(i)
                    }
                    placeholder!!.visibility = if (tableManager.hasTimetables()) View.GONE else View.VISIBLE
                },
                onError = {
                    apiOnError(it)
                }
            )
    }

    private fun getAdapter(tables: List<TableDto>): ExpandableTableListAdapter {
        mGroupList = ArrayList()
        mChildList = ArrayList()
        for (coursebook in coursebookList!!) {
            mGroupList!!.add(getFullSemester(coursebook.year.toInt(), coursebook.semester.toInt()))
            mChildList!!.add(ArrayList())
        }
        var index = 0
        for (table in tables) {
            while (index < mGroupList!!.size && mGroupList!![index] != SNUTTStringUtils.getFullSemester(table)) index++
            if (index >= mGroupList!!.size) break
            mChildList!![index].add(table)
        }
        return ExpandableTableListAdapter(this, mGroupList, mChildList, prefStorage)
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
