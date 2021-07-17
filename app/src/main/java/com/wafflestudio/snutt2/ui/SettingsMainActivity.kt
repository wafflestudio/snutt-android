package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.common.base.Preconditions
import com.google.common.base.Verify
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserSettingsFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by makesource on 2016. 11. 20..
 */
@AndroidEntryPoint
class SettingsMainActivity : SNUTTBaseActivity(), FragmentManager.OnBackStackChangedListener {
    private val currentFragmentIndex: Int
        private get() {
            var fragment: Fragment?
            for (i in 0 until FRAGMENT_NUMS) {
                Verify.verifyNotNull(FRAGMENT_TAGS[i])
                fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAGS[i])
                if (fragment != null && fragment.isVisible) {
                    return i
                }
            }
            Log.e(TAG, "Cannot find current visible fragment!!!!")
            return FRAGMENT_ERROR
        }

    private fun setTitle() {
        val index = currentFragmentIndex
        when (index) {
            FRAGMENT_ACCOUNT -> supportActionBar!!.setTitle("계정관리")
            FRAGMENT_TIMETABLE -> supportActionBar!!.setTitle("시간표 설정")
            FRAGMENT_DEVELOPER -> supportActionBar!!.setTitle("개발자 정보")
            FRAGMENT_REPORT -> supportActionBar!!.setTitle("개발자 괴롭히기")
            FRAGMENT_LICENSE -> supportActionBar!!.setTitle("라이센스 정보")
            FRAGMENT_TERMS -> supportActionBar!!.setTitle("서비스 약관")
            FRAGMENT_PRIVACY -> supportActionBar!!.setTitle("개인정보처리방침")
            else -> Log.e(TAG, "Fragment error!!!!")
        }
    }

    private fun setAccountFragment() {
        showFragment(FRAGMENT_ACCOUNT, false)
        supportActionBar!!.title = "계정관리"
    }

    private fun setTimetableFragment() {
        showFragment(FRAGMENT_TIMETABLE, false)
        supportActionBar!!.title = "시간표 설정"
    }

    private fun setDeveloperFragment() {
        showFragment(FRAGMENT_DEVELOPER, false)
        supportActionBar!!.title = "개발자 정보"
    }

    private fun setReportFragment() {
        showFragment(FRAGMENT_REPORT, false)
        supportActionBar!!.title = "개발자 괴롭히기"
    }

    private fun setLicenseFragment() {
        showFragment(FRAGMENT_LICENSE, false)
        supportActionBar!!.title = "라이센스 정보"
    }

    private fun setTermsFragment() {
        showFragment(FRAGMENT_TERMS, false)
        supportActionBar!!.title = "서비스 약관"
    }

    private fun setPrivacyFragment() {
        showFragment(FRAGMENT_PRIVACY, false)
        supportActionBar!!.title = "개인정보처리방침"
    }

    private fun newFragment(fragmentIdx: Int): Fragment? {
        return when (fragmentIdx) {
            FRAGMENT_ACCOUNT -> UserSettingsFragment()
            FRAGMENT_TIMETABLE -> TimetableFragment()
            FRAGMENT_DEVELOPER -> DeveloperFragment()
            FRAGMENT_REPORT -> ReportFragment()
            FRAGMENT_LICENSE -> LicenseFragment()
            FRAGMENT_TERMS -> TermsFragment()
            FRAGMENT_PRIVACY -> PrivacyFragment()
            else -> {
                Log.e(TAG, "Fragment index is out of range!!!")
                null
            }
        }
    }

    private fun showFragment(fragmentIdx: Int, withBackStackPush: Boolean) {
        Preconditions.checkArgument(fragmentIdx >= 0)
        Preconditions.checkArgument(fragmentIdx < FRAGMENT_NUMS)
        val fragmentTag = FRAGMENT_TAGS[fragmentIdx]
        val fragment = newFragment(fragmentIdx)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.settings_main_layout, fragment!!, fragmentTag)
        if (withBackStackPush) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityList.add(this)
        setContentView(R.layout.activity_settings_main)
        supportFragmentManager.addOnBackStackChangedListener(this)
        val type = intent.getIntExtra(INTENT_KEY_SETTINGS_TYPE, -1)
        Preconditions.checkArgument(type != -1)
        when (type) {
            FRAGMENT_ACCOUNT -> setAccountFragment()
            FRAGMENT_TIMETABLE -> setTimetableFragment()
            FRAGMENT_DEVELOPER -> setDeveloperFragment()
            FRAGMENT_REPORT -> setReportFragment()
            FRAGMENT_LICENSE -> setLicenseFragment()
            FRAGMENT_TERMS -> setTermsFragment()
            FRAGMENT_PRIVACY -> setPrivacyFragment()
            else -> {
            }
        }

        /* if (position == -1) { // c
            lecture = null;
            setCustomDetailFragment();
        } else {
            lecture = LectureManager.getInstance().getLectures().get(position);
            if (lecture.isCustom()) setCustomDetailFragment();
            else setMainFragment();
        }*/
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    override fun onBackStackChanged() {
        setTitle()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
    }

    companion object {
        private const val TAG = "SETTING_MAIN_ACTIVITY"
        private const val TAG_FRAGMENT_ACCOUNT = "TAG_FRAGMENT_ACCOUNT"
        private const val TAG_FRAGMENT_TIMETABLE = "TAG_FRAGMENT_TIMETABLE"
        private const val TAG_FRAGMENT_DEVELOPER = "TAG_FRAGMENT_DEVELOPER"
        private const val TAG_FRAGMENT_REPORT = "TAG_FRAGMENT_REPORT"
        private const val TAG_FRAGMENT_LICENSE = "TAG_FRAGMENT_LICENSE"
        private const val TAG_FRAGMENT_TERMS = "TAG_FRAGMENT_TERMS"
        private const val TAG_FRAGMENT_PRIVACY = "TAG_FRAGMENT_PRIVACY"
        private val FRAGMENT_TAGS = arrayOf(
            TAG_FRAGMENT_ACCOUNT,
            TAG_FRAGMENT_TIMETABLE,
            TAG_FRAGMENT_DEVELOPER,
            TAG_FRAGMENT_REPORT,
            TAG_FRAGMENT_LICENSE,
            TAG_FRAGMENT_TERMS,
            TAG_FRAGMENT_PRIVACY
        )
        const val FRAGMENT_ERROR = -1
        const val FRAGMENT_ACCOUNT = 0
        const val FRAGMENT_TIMETABLE = 1
        const val FRAGMENT_DEVELOPER = 2
        const val FRAGMENT_REPORT = 3
        const val FRAGMENT_LICENSE = 4
        const val FRAGMENT_TERMS = 5
        const val FRAGMENT_PRIVACY = 6
        private const val FRAGMENT_NUMS = 7
    }
}
