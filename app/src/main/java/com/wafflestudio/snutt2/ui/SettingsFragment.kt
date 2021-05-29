package com.wafflestudio.snutt2.ui

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.adapter.SettingsAdapter
import com.wafflestudio.snutt2.manager.UserManager
import com.wafflestudio.snutt2.model.SettingsItem
import dagger.hilt.android.AndroidEntryPoint
import de.psdev.licensesdialog.LicensesDialog
import java.util.*
import javax.inject.Inject

/**
 * Created by makesource on 2016. 1. 16..
 */
@AndroidEntryPoint
class SettingsFragment : SNUTTBaseFragment() {

    @Inject
    lateinit var userManager: UserManager

    private var lists: MutableList<SettingsItem>? = null
    private var adapter: SettingsAdapter? = null
    private var clickListener: SettingsAdapter.ClickListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lists = ArrayList()
        lists!!.add(SettingsItem(SettingsItem.Type.Header))
        lists!!.add(SettingsItem("계정관리", SettingsItem.Type.Account))
        lists!!.add(SettingsItem("시간표 설정", SettingsItem.Type.Timetable))
        lists!!.add(SettingsItem(SettingsItem.Type.Header))
        lists!!.add(SettingsItem("버전 정보", SettingsItem.Type.Version))
        lists!!.add(SettingsItem(SettingsItem.Type.Header))
        lists!!.add(SettingsItem("개발자 정보", SettingsItem.Type.Developer))
        lists!!.add(SettingsItem("개발자 괴롭히기", SettingsItem.Type.BugReport))
        lists!!.add(SettingsItem(SettingsItem.Type.Header))
        lists!!.add(SettingsItem("라이센스 정보", SettingsItem.Type.License))
        lists!!.add(SettingsItem("서비스 약관", SettingsItem.Type.Terms))
        lists!!.add(SettingsItem("개인정보처리방침", SettingsItem.Type.Private))
        lists!!.add(SettingsItem(SettingsItem.Type.Header))
        lists!!.add(SettingsItem("로그아웃", SettingsItem.Type.Logout))
        // lists.add(new SettingsItem(SettingsItem.Type.Header));
        adapter = SettingsAdapter(lists!!)
        clickListener = object : SettingsAdapter.ClickListener {
            override fun onClick(v: View?, position: Int) {
                Log.d(TAG, "$position-th item clicked!")
                val type = lists!!.get(position).type
                when (type) {
                    SettingsItem.Type.Account -> mainActivity!!.startSettingsMain(
                        SettingsMainActivity.FRAGMENT_ACCOUNT
                    )
                    SettingsItem.Type.Timetable -> mainActivity!!.startSettingsMain(
                        SettingsMainActivity.FRAGMENT_TIMETABLE
                    )
                    SettingsItem.Type.Developer -> mainActivity!!.startSettingsMain(
                        SettingsMainActivity.FRAGMENT_DEVELOPER
                    )
                    SettingsItem.Type.BugReport -> mainActivity!!.startSettingsMain(
                        SettingsMainActivity.FRAGMENT_REPORT
                    )
                    SettingsItem.Type.License -> showLicenseDialog()
                    SettingsItem.Type.Terms -> mainActivity!!.startSettingsMain(
                        SettingsMainActivity.FRAGMENT_TERMS
                    )
                    SettingsItem.Type.Private -> mainActivity!!.startSettingsMain(
                        SettingsMainActivity.FRAGMENT_PRIVACY
                    )
                    SettingsItem.Type.Logout -> performLogout()
                    else -> {
                    }
                }
            }
        }

        /*
        // 서버에서 version name 가져옴
        UserManager.getInstance().getAppVersion(new Callback<Version>() {
            @Override
            public void success(Version version, Response response) {
                updateVersion(version.getVersion());
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
        */try {
            val packageName = app.packageName
            val appVersion = app.packageManager.getPackageInfo(packageName, 0).versionName
            updateVersion(appVersion)
        } catch (e: PackageManager.NameNotFoundException) {
            updateVersion("2.0.0")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        // ListView listView = (ListView) rootView.findViewById(R.id.settings_list);
        // listView.setAdapter(adapter);
        val recyclerView = rootView.findViewById<View>(R.id.settings_recyclerView) as RecyclerView
        // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return rootView
    }

    override fun onResume() {
        Log.d(TAG, "on resume called!")
        super.onResume()
        adapter!!.setOnItemClickListener(clickListener!!)
    }

    private fun updateVersion(version: String) {
        var position = -1
        for (i in lists!!.indices) {
            if (lists!![i].type === SettingsItem.Type.Version) position = i
        }
        if (position == -1) {
            Log.e(TAG, "Version item does not exists!")
            return
        }
        lists!![position].detail = version
        adapter!!.notifyItemChanged(position)
    }

    private fun performLogout() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("로그아웃")
        alert.setMessage("로그아웃 하시겠습니까?")
        alert.setPositiveButton("로그아웃") { dialog, whichButton ->
            val progressDialog = ProgressDialog.show(context, "로그아웃", "잠시만 기다려 주세요", true, false)
            userManager.deleteFirebaseToken()
                .bindUi(
                    this,
                    onSuccess = {
                        userManager.performLogout()
                        mainActivity!!.startIntro()
                        mainActivity!!.finishAll()
                        progressDialog.dismiss()
                    },
                    onError = {
                        Toast.makeText(app, "로그아웃에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                )
        }.setNegativeButton("취소") { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun showLicenseDialog() {
        LicensesDialog.Builder(context)
            .setNotices(R.raw.notices)
            .setIncludeOwnLicense(true)
            .build()
            .show()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val TAG = "SETTINGS_FRAGMENT"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}
