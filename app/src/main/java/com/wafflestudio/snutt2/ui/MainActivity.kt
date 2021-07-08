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
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener
import com.google.common.base.Strings
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.adapter.SectionsPagerAdapter
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.manager.NotiManager
import com.wafflestudio.snutt2.manager.NotiManager.OnNotificationReceivedListener
import com.wafflestudio.snutt2.manager.PrefStorage
import com.wafflestudio.snutt2.manager.TableManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : SNUTTBaseActivity(), OnNotificationReceivedListener {

    @Inject
    lateinit var prefStorage: PrefStorage

    @Inject
    lateinit var lectureManager: LectureManager

    @Inject
    lateinit var notiManager: NotiManager

    @Inject
    lateinit var tableManager: TableManager

    @Inject
    lateinit var apiOnError: ApiOnError

    internal enum class MainTab(val title: String) {
        TIMETABLE("시간표"), SEARCH("검색"), MY_LECTURE("내 강의"), NOTIFICATION("알림"), SETTING("설정");
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private lateinit var tabLayout: TabLayout

    private lateinit var mViewPager: ViewPager

    private var notiCircle: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate called!")
        activityList.add(this)
        notiManager.addListener(this)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        // Set up the ViewPager with the sections adapter.
        val appBarLayout = findViewById<View>(R.id.appbar) as AppBarLayout
        mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager.adapter = mSectionsPagerAdapter
        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)
        setTabLayoutView()
        setUpAppBarLayout(appBarLayout)

        // 1. token 의 유무 검사
        if (prefStorage.prefKeyXAccessToken == null) {
            // 로그인 창으로 이동
            // startWelcome();
            startIntro()
            finish()
            return
        }

        // 2. colorList 받아오기
        lectureManager.fetchColorList("vivid_ios")
            .bindUi(this, {}, {})

        // 3. 앱 내부에 저장된 시간표 뛰어주기
        // TODO : 저장된 정보를 불러와 보여주기, 없으면 empty상태로 띄어준다.
        prefStorage.currentTable?.let {
            supportActionBar?.setTitle(it.title)
            lectureManager.setLectures(it.lectureList ?: emptyList())
        }

        // 4. 서버에서 시간표 정보 얻어오기
        // TODO : 서버에서 마지막에 본 시간표 정보 받아오기
        val id: String? = prefStorage.lastViewTableId
        if (id != null) {
            tableManager.getTableById(id)
                .bindUi(
                    this,
                    onSuccess = {
                        supportActionBar?.setTitle(it.title)
                    },
                    onError = {
                        // invalid token -> 로그인 화면으로
                        // invalid id -> 없어진 테이블
                        apiOnError(it)
                    }
                )
        } else {
            // 처음 로그인한 경우 -> 서버에서 default값을 요청
            tableManager.getDefaultTable()
                .bindUi(
                    this,
                    onSuccess = {
                        supportActionBar?.setTitle(it.title)
                    },
                    onError = {
                        // default 가 존재하지 않거나, network error 인 경우
                        apiOnError(it)
                        supportActionBar?.setTitle("empty table")
                    }
                )
        }

        // noti check
        notiManager.getNotificationCount()
            .bindUi(
                this,
                onSuccess = {
                    val count = it.count
                    Log.d(TAG, "notification count : $count")
                    if (notiCircle != null) {
                        notiCircle!!.visibility = if (count > 0) View.VISIBLE else View.GONE
                    }
                },
                onError = {
                    apiOnError(it)
                }
            )

        toolbar.setOnClickListener {
            val type = MainTab.values()[mViewPager.currentItem]
            if (type == MainTab.TIMETABLE) {
                showEditDialog(prefStorage.lastViewTableId!!)
            }
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // TableListActivity에서 들어 오는 경우 -> 선택한 table 을 보여줘야 함
        val id: String = intent.extras?.getString(INTENT_KEY_TABLE_ID) ?: return

        // 서버에서 받아와서 다시 그리기
        tableManager.getTableById(id)
            .bindUi(
                this,
                onSuccess = { supportActionBar?.title = it.title },
                onError = {
                    apiOnError(it)
                }
            )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
        notiManager.removeListener(this)
    }

    public override fun onResume() {
        super.onResume()
        updateTableTitle()
    }

    private fun updateTableTitle() {
        val id: String = prefStorage.lastViewTableId ?: return
        val title = tableManager.getTableTitleById(id)
        if (!Strings.isNullOrEmpty(title)) {
            supportActionBar!!.setTitle(title)
        }
    }

    private fun setTabLayoutView() {
        for (i in 0 until tabLayout.tabCount) {
            val layout = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as FrameLayout
            val textView = layout.findViewById<View>(R.id.tab_title) as TextView
            textView.text = getPageTitle(i)
            val imageView = layout.findViewById<View>(R.id.noti) as ImageView
            imageView.visibility = View.GONE
            tabLayout.getTabAt(i)?.customView = layout
            val type = MainTab.values()[i]
            if (type == MainTab.NOTIFICATION) {
                notiCircle = imageView
            }
        }
        // for initial state
        tabLayout.getTabAt(MainTab.TIMETABLE.ordinal)!!.customView!!.isSelected = true
        tabLayout.setOnTabSelectedListener(
            object : ViewPagerOnTabSelectedListener(mViewPager) {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    super.onTabSelected(tab)
                    Log.d(TAG, "on tab selected!")
                    val type = MainTab.values()[tab.position]
                    if (type == MainTab.NOTIFICATION) {
                        onNotificationChecked()
                    }
                }
            }
        )
    }

    private fun showEditDialog(id: String) {
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
                tableManager.putTable(id, title)
                    .bindUi(
                        this,
                        onSuccess = {
                            supportActionBar!!.setTitle(title)
                            dialog.dismiss()
                        },
                        onError = {
                            apiOnError(it)
                        }
                    )
            } else {
                Toast.makeText(app, "시간표 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpAppBarLayout(appBarLayout: AppBarLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { // for pre-lollipop device
            appBarLayout.setBackgroundDrawable(
                resources.getDrawable(R.drawable.actionbar_background)
            )
        }
    }

    fun showTabLayout() {
        tabLayout.visibility = View.VISIBLE
    }

    fun hideTabLayout() {
        tabLayout.visibility = View.GONE
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
        val type = MainTab.values()[tabLayout.selectedTabPosition]
        if (notiCircle != null && type != MainTab.NOTIFICATION) {
            runOnUiThread { notiCircle!!.visibility = View.VISIBLE }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }
}
