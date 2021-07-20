package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.UserRepository
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.model.SettingsItem
import dagger.hilt.android.AndroidEntryPoint
import de.psdev.licensesdialog.LicensesDialog
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by makesource on 2016. 1. 16..
 */
@Suppress("DEPRECATION")
@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    @Inject
    lateinit var userRepository: UserRepository

    private var lists: MutableList<SettingsItem> = mutableListOf()
    private lateinit var adapter: UserSettingsAdapter
    private var clickListener: UserSettingsAdapter.ClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lists.clear()
        lists.add(SettingsItem(SettingsItem.Type.Header))
        lists.add(SettingsItem("계정관리", SettingsItem.Type.Account))
        lists.add(SettingsItem("시간표 설정", SettingsItem.Type.Timetable))
        lists.add(SettingsItem(SettingsItem.Type.Header))
        lists.add(SettingsItem("버전 정보", SettingsItem.Type.Version))
        lists.add(SettingsItem(SettingsItem.Type.Header))
        lists.add(SettingsItem("개발자 정보", SettingsItem.Type.Developer))
        lists.add(SettingsItem("개발자 괴롭히기", SettingsItem.Type.BugReport))
        lists.add(SettingsItem(SettingsItem.Type.Header))
        lists.add(SettingsItem("라이센스 정보", SettingsItem.Type.License))
        lists.add(SettingsItem("서비스 약관", SettingsItem.Type.Terms))
        lists.add(SettingsItem("개인정보처리방침", SettingsItem.Type.Private))
        lists.add(SettingsItem(SettingsItem.Type.Header))
        lists.add(SettingsItem("로그아웃", SettingsItem.Type.Logout))
        // lists.add(new SettingsItem(SettingsItem.Type.Header));
        adapter = UserSettingsAdapter(lists)
        clickListener = object : UserSettingsAdapter.ClickListener {
            override fun onClick(v: View?, position: Int) {
                Timber.d("$position-th item clicked!")
                when (lists[position].type) {
                    SettingsItem.Type.Account -> routeUserSettings()
                    SettingsItem.Type.Timetable -> routeTimetableSettings()
                    SettingsItem.Type.Developer -> {
                        routeTeamInfo()
                    }
                    SettingsItem.Type.BugReport -> {
                        // TODO
                    }
                    SettingsItem.Type.License -> showLicenseDialog()
                    SettingsItem.Type.Terms -> {
                        routeTermsInfo()
                    }
                    SettingsItem.Type.Private -> {
                        routeServiceInfo()
                    }
                    SettingsItem.Type.Logout -> performLogout()
                    else -> {
                    }
                }
            }
        }

        try {
            val packageName = requireContext().packageName
            val appVersion =
                requireContext().packageManager.getPackageInfo(packageName, 0).versionName
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
        Timber.d("on resume called!")
        super.onResume()
        adapter.setOnItemClickListener(clickListener!!)
    }

    private fun updateVersion(version: String) {
        var position = -1
        for (i in lists.indices) {
            if (lists[i].type === SettingsItem.Type.Version) position = i
        }
        if (position == -1) {
            Timber.e("Version item does not exists!")
            return
        }
        lists[position].detail = version
        adapter.notifyItemChanged(position)
    }

    private fun performLogout() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("로그아웃")
        alert.setMessage("로그아웃 하시겠습니까?")
        alert.setPositiveButton("로그아웃") { _, _ ->
            val progressDialog = ProgressDialog.show(context, "로그아웃", "잠시만 기다려 주세요", true, false)
            userRepository.deleteFirebaseToken()
                .bindUi(
                    this,
                    onSuccess = {
                        userRepository.performLogout()
                        progressDialog.dismiss()
                    },
                    onError = {
                        Toast.makeText(context, "로그아웃에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                )
        }.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun routeUserSettings() {
        findNavController().navigate(R.id.action_homeFragment_to_userSettingsFragment)
    }

    private fun routeTimetableSettings() {
        findNavController().navigate(R.id.action_homeFragment_to_timetableSettingsFragment)
    }

    private fun routeTeamInfo() {
        findNavController().navigate(R.id.action_homeFragment_to_teamInfoFragment)
    }

    private fun routeServiceInfo() {
        findNavController().navigate(R.id.action_homeFragment_to_serviceInfoFragment)
    }

    private fun routeTermsInfo() {
        findNavController().navigate(R.id.action_homeFragment_to_termsInfoFragment)
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
