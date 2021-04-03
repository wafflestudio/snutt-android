package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.manager.TableManager.Companion.instance
import com.wafflestudio.snutt2.model.Coursebook
import com.wafflestudio.snutt2.model.Table
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 * Created by makesource on 2016. 3. 1..
 */
class TableCreateActivity : SNUTTBaseActivity() {
    private var year = -1
    private var semester = -1
    private var titleText: EditText? = null
    private var semesterSpinner: Spinner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityList.add(this)
        setContentView(R.layout.activity_table_create)
        title = "새로운 시간표"
        semesterSpinner = findViewById<View>(R.id.spinner) as Spinner
        titleText = findViewById<View>(R.id.table_title) as EditText
        instance!!.getCoursebook(
            object : Callback<List<Coursebook>> {
                override fun success(coursebooks: List<Coursebook>, response: Response) {
                    val displays = getDisplayList(coursebooks)
                    val years = getYearList(coursebooks)
                    val semesters = getSemesterList(coursebooks)
                    year = years[0]
                    semester = semesters[0]
                    val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, displays)
                    semesterSpinner!!.adapter = adapter
                    semesterSpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                            year = years[position]
                            semester = semesters[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }

                override fun failure(error: RetrofitError) {}
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
    }

    private fun getDisplayList(coursebooks: List<Coursebook>): Array<String?> {
        val size = coursebooks.size
        val list = arrayOfNulls<String>(size)
        for (i in 0 until size) {
            val year: String = coursebooks[i].year.toString() + " 년"
            val semester = getSemester(coursebooks[i].semester)
            list[i] = "$year $semester"
        }
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_table_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_create) {
            val title = titleText!!.text.toString()
            instance!!.postTable(
                year,
                semester,
                title,
                object : Callback<List<Table>> {
                    override fun success(tables: List<Table>?, response: Response) {
                        finish()
                    }

                    override fun failure(error: RetrofitError) {}
                }
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getSemester(semester: Int): String {
        return when (semester) {
            1 -> "1학기"
            2 -> "여름학기"
            3 -> "2학기"
            4 -> "겨울학기"
            else -> "(null)"
        }
    }

    private fun getYearList(coursebooks: List<Coursebook>): IntArray {
        val size = coursebooks.size
        val list = IntArray(size)
        for (i in 0 until size) {
            list[i] = coursebooks[i].year
        }
        return list
    }

    private fun getSemesterList(coursebooks: List<Coursebook>): IntArray {
        val size = coursebooks.size
        val list = IntArray(size)
        for (i in 0 until size) {
            list[i] = coursebooks[i].semester
        }
        return list
    }
}
