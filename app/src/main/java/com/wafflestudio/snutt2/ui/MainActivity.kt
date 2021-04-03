package com.wafflestudio.snutt2.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener
import com.google.common.base.Strings
import com.google.gson.Gson
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.adapter.SectionsPagerAdapter
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.manager.NotiManager
import com.wafflestudio.snutt2.manager.NotiManager.OnNotificationReceivedListener
import com.wafflestudio.snutt2.manager.PrefManager
import com.wafflestudio.snutt2.manager.TableManager
import com.wafflestudio.snutt2.model.Table
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

class MainActivity : SNUTTBaseActivity(), OnNotificationReceivedListener {
    internal enum class MainTab(val title: String) {
        TIMETABLE("시간표"), SEARCH("검색"), MY_LECTURE("내 강의"), NOTIFICATION("알림"), SETTING("설정");
    }

    /**
     * The [PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The [ViewPager] that will host the section contents.
     */
    protected var tabLayout: TabLayout? = null
    private var mViewPager: ViewPager? = null
    private var notiCircle: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate called!")
        activityList.add(this)
        NotiManager.instance!!.addListener(this)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        // Set up the ViewPager with the sections adapter.
        val appBarLayout = findViewById<View>(R.id.appbar) as AppBarLayout
        mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter
        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout!!.setupWithViewPager(mViewPager)
        setTabLayoutView(tabLayout)
        setUpAppBarLayout(appBarLayout)

        // 1. token 의 유무 검사
        if (PrefManager.instance!!.prefKeyXAccessToken == null) {
            // 로그인 창으로 이동
            //startWelcome();
            startIntro()
            finish()
            return
        }

        // 2. colorList 받아오기
        LectureManager.instance!!.fetchColorList("vivid_ios", null)

        // 3. 앱 내부에 저장된 시간표 뛰어주기
        // TODO : 저장된 정보를 불러와 보여주기, 없으면 empty상태로 띄어준다.
        val json: String? = PrefManager.instance!!.currentTable
        if (json != null) {
            val table = Gson().fromJson(json, Table::class.java)
            supportActionBar!!.setTitle(table.title)
            LectureManager.instance!!.setLectures(table.lecture_list!!)
        }

        // 4. 서버에서 시간표 정보 얻어오기
        // TODO : 서버에서 마지막에 본 시간표 정보 받아오기
        val id: String? = PrefManager.instance!!.lastViewTableId
        if (id != null) {
            TableManager.instance!!.getTableById(id, object : Callback<Table> {
                override fun success(table: Table, response: Response) {
                    supportActionBar!!.setTitle(table.title)
                }

                override fun failure(error: RetrofitError) {
                    // invalid token -> 로그인 화면으로
                    // invalid id -> 없어진 테이블
                }
            })
        } else {
            // 처음 로그인한 경우 -> 서버에서 default값을 요청
            TableManager.instance!!.getDefaultTable(object : Callback<Table> {
                override fun success(table: Table, response: Response) {
                    // default 가 존재하는 경우
                    supportActionBar!!.setTitle(table.title)
                }

                override fun failure(error: RetrofitError) {
                    // default가 존재하지 않거나, network error 인 경우
                    supportActionBar!!.setTitle("empty table")
                }
            })
        }

        // noti check
        NotiManager.instance!!.getNotificationCount(object : Callback<Map<String?, Int?>> {
            override fun success(map: Map<String?, Int?>, response: Response) {
                val count = map["count"]!!
                Log.d(TAG, "notification count : $count")
                if (notiCircle != null) {
                    notiCircle!!.visibility = if (count > 0) View.VISIBLE else View.GONE
                }
            }

            override fun failure(error: RetrofitError) {}
        })
        toolbar.setOnClickListener {
            val type = MainTab.values()[mViewPager!!.currentItem]
            if (type == MainTab.TIMETABLE) {
                showEditDialog(PrefManager.instance!!.lastViewTableId!!)
            }
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // TableListActivity에서 들어 오는 경우 -> 선택한 table 을 보여줘야 함
        var id: String? = null
        if (intent.extras != null) { // intent의 강의 id 받아오기
            id = intent.extras!!.getString(INTENT_KEY_TABLE_ID)
        }
        if (id == null) {
            Log.e(TAG, "intent has no table id!!")
            return
        }
        // 서버에서 받아와서 다시 그리기
        TableManager.instance!!.getTableById(id, object : Callback<Table> {
            override fun success(table: Table, response: Response) {
                supportActionBar!!.setTitle(table.title)
            }

            override fun failure(error: RetrofitError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
        NotiManager.instance!!.removeListener(this)
    }

    public override fun onResume() {
        super.onResume()
        checkGoogleServiceVersion()
        updateTableTitle()
    }

    private fun updateTableTitle() {
        val id: String = PrefManager.instance?.lastViewTableId ?: return
        val title = TableManager.instance!!.getTableTitleById(id)
        if (!Strings.isNullOrEmpty(title)) {
            supportActionBar!!.setTitle(title)
        }
    }

    private fun checkGoogleServiceVersion(): Boolean {
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if (resultCode == ConnectionResult.SUCCESS) {
            Log.d(TAG, "google play service is available.")
            return true
        }
        if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
            Log.d(TAG, "google play service is user resolvable error.")
            GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST) { checkGoogleServiceVersion() }.show()
        } else {
            Log.e(TAG, "google play service is not supported in this device.")
        }
        return false
    }

    private fun setTabLayoutView(tabLayout: TabLayout?) {
        for (i in 0 until tabLayout!!.tabCount) {
            val layout = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as FrameLayout
            val textView = layout.findViewById<View>(R.id.tab_title) as TextView
            textView.text = getPageTitle(i)
            val imageView = layout.findViewById<View>(R.id.noti) as ImageView
            imageView.visibility = View.GONE
            tabLayout.getTabAt(i)!!.customView = layout
            val type = MainTab.values()[i]
            if (type == MainTab.NOTIFICATION) {
                notiCircle = imageView
            }
        }
        // for initial state
        tabLayout.getTabAt(MainTab.TIMETABLE.ordinal)!!.customView!!.isSelected = true
        tabLayout.setOnTabSelectedListener(object : ViewPagerOnTabSelectedListener(mViewPager) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                super.onTabSelected(tab)
                Log.d(TAG, "on tab selected!")
                val type = MainTab.values()[tab.position]
                if (type == MainTab.NOTIFICATION) {
                    onNotificationChecked()
                }
            }
        })
    }

    private fun showEditDialog(id: String) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.dialog_change_title, null)
        val alert = AlertDialog.Builder(this)
        alert.setTitle("시간표 이름 변경")
        alert.setView(layout)
        alert.setPositiveButton("변경") { dialog, whichButton ->
            // do nothing in here. because we override this button listener later
        }.setNegativeButton("취소"
        ) { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = (layout.findViewById<View>(R.id.title) as EditText).text.toString()
            if (!Strings.isNullOrEmpty(title)) {
                TableManager.instance!!.putTable(id, title, object : Callback<List<Table>> {
                    override fun success(tables: List<Table>?, response: Response) {
                        supportActionBar!!.setTitle(title)
                        dialog.dismiss()
                    }

                    override fun failure(error: RetrofitError) {
                        //show error message
                    }
                })
            } else {
                Toast.makeText(app, "시간표 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpAppBarLayout(appBarLayout: AppBarLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { // for pre-lollipop device
            appBarLayout.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar_background))
        }
    }

    fun showTabLayout() {
        tabLayout!!.visibility = View.VISIBLE
    }

    fun hideTabLayout() {
        tabLayout!!.visibility = View.GONE
    }

    fun getPageTitle(position: Int): String {
        val type = MainTab.values()[position]
        return type.title
    }

    fun onNotificationChecked() {
        Log.d(TAG, "on notification checked!")
        if (notiCircle != null) {
            notiCircle!!.visibility = View.GONE
        }
    }

    override fun notifyNotificationReceived() {
        Log.d(TAG, "on notification received")
        val type = MainTab.values()[tabLayout!!.selectedTabPosition]
        if (notiCircle != null && type != MainTab.NOTIFICATION) {
            runOnUiThread { notiCircle!!.visibility = View.VISIBLE }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }
}