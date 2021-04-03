package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.common.base.Preconditions
import com.google.common.base.Verify
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.manager.LectureManager.Companion.instance
import com.wafflestudio.snutt2.model.Color
import com.wafflestudio.snutt2.model.LectureItem
import com.wafflestudio.snutt2.ui.ColorPickerFragment.ColorChangedListener

/**
 * Created by makesource on 2016. 3. 1..
 */
class LectureMainActivity : SNUTTBaseActivity(), FragmentManager.OnBackStackChangedListener, ColorChangedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityList.add(this)
        setContentView(R.layout.activity_lecture_main)
        supportFragmentManager.addOnBackStackChangedListener(this)
        val position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1)
        if (position == -1) { // create custom lecture
            instance!!.currentLecture = null
            setCustomDetailFragment()
        } else {
            val lecture = instance!!.getLectures()[position]
            instance!!.currentLecture = lecture
            if (lecture.isCustom) setCustomDetailFragment() else setMainFragment()
        }
    }

    private fun setMainFragment() {
        val fragment: Fragment = LectureDetailFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_lecture_main, fragment, TAG_FRAGMENT_LECTURE_DETAIL)
        transaction.commit()
        supportActionBar!!.title = "강의 상세 보기"
    }

    fun setColorPickerFragment(item: LectureItem) {
        val bundle = Bundle()
        bundle.putInt("index", item.colorIndex)
        showFragment(FRAGMENT_COLOR_PICKER, true, bundle)
    }

    fun setCustomDetailFragment() {
        val fragment: Fragment = CustomDetailFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_lecture_main, fragment, TAG_FRAGMENT_CUSTOM_DETAIL)
        transaction.commit()
        val lecture = instance!!.currentLecture
        if (lecture == null) supportActionBar!!.title = "커스텀 강의 추가" else supportActionBar!!.title = "강의 상세 보기"
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    override fun onBackStackChanged() {
        setTitle()
    }

    // public
    private val currentFragmentIndex: Int
        private get() {
            var fragment: Fragment?
            for (i in 0 until FRAGMENT_ROOM_NUM) {
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
        val lecture = instance!!.currentLecture
        val index = currentFragmentIndex
        when (index) {
            FRAGMENT_LECTURE_DETAIL -> supportActionBar!!.setTitle("강의 상세 보기")
            FRAGMENT_COLOR_PICKER -> supportActionBar!!.setTitle("강의 색상 변경")
            FRAGMENT_CUSTOM_DETAIL -> if (lecture == null) supportActionBar!!.title = "커스텀 강의 추가" else supportActionBar!!.title = "강의 상세 보기"
            else -> Log.e(TAG, "Fragment error!!!!")
        }
    }

    private fun newFragment(fragmentIdx: Int): Fragment? {
        return when (fragmentIdx) {
            FRAGMENT_LECTURE_DETAIL -> LectureDetailFragment.newInstance()
            FRAGMENT_COLOR_PICKER -> ColorPickerFragment.newInstance()
            FRAGMENT_CUSTOM_DETAIL -> CustomDetailFragment.newInstance()
            else -> {
                Log.e(TAG, "Fragment index is out of range!!!")
                null
            }
        }
    }

    private fun showFragment(fragmentIdx: Int, withBackStackPush: Boolean, bundle: Bundle) {
        Preconditions.checkArgument(fragmentIdx >= 0)
        Preconditions.checkArgument(fragmentIdx < FRAGMENT_ROOM_NUM)
        val fragmentTag = FRAGMENT_TAGS[fragmentIdx]
        val fragment = newFragment(fragmentIdx)
        fragment!!.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.layout_rightin,
            R.anim.layout_leftout,
            R.anim.layout_leftin,
            R.anim.layout_rightout
        )
        transaction.replace(R.id.activity_lecture_main, fragment, fragmentTag)
        if (withBackStackPush) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Log.d(TAG, "on back pressed called")
        val index = currentFragmentIndex
        if (index == FRAGMENT_LECTURE_DETAIL) {
            val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAGS[index]) as LectureDetailFragment?
            if (fragment!!.editable) {
                val alert = AlertDialog.Builder(this)
                alert.setPositiveButton("확인") { dialog, which ->
                    fragment.refreshFragment()
                    dialog.dismiss()
                }.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }.setTitle(
                    "편집을 취소하시겠습니까?"
                )
                alert.show()
                return
            }
        } else if (index == FRAGMENT_CUSTOM_DETAIL) {
            val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAGS[index]) as CustomDetailFragment?
            if (fragment!!.getEditable()) {
                val alert = AlertDialog.Builder(this)
                alert.setPositiveButton("확인") { dialog, which ->
                    fragment.refreshFragment()
                    dialog.dismiss()
                }.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }.setTitle(
                    "편집을 취소하시겠습니까?"
                )
                alert.show()
                return
            }
        }
        super.onBackPressed()
    }

    override fun onColorChanged(index: Int, color: Color?) {
        val lecture = instance!!.currentLecture
        if (lecture == null || lecture.isCustom) {
            val fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CUSTOM_DETAIL) as CustomDetailFragment?
            fragment!!.setLectureColor(index, color)
        } else {
            val fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_LECTURE_DETAIL) as LectureDetailFragment?
            fragment!!.setLectureColor(index, color)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
    }

    companion object {
        private const val TAG = "LECTURE_MAIN_ACTIVITY"
        const val TAG_FRAGMENT_LECTURE_DETAIL = "TAG_FRAGMENT_LECTURE_DETAIL"
        const val TAG_FRAGMENT_COLOR_PICKER = "TAG_FRAGMENT_COLOR_PICKER"
        const val TAG_FRAGMENT_CUSTOM_DETAIL = "TAG_FRAGMENT_CUSTOM_DETAIL"

        // public final static String TAG_FRAGMENT_TEST = "TAG_FRAGMENT_TEST";
        private val FRAGMENT_TAGS = arrayOf(
            TAG_FRAGMENT_LECTURE_DETAIL,
            TAG_FRAGMENT_COLOR_PICKER,
            TAG_FRAGMENT_CUSTOM_DETAIL // TAG_FRAGMENT_TEST
        )
        const val FRAGMENT_ERROR = -1
        const val FRAGMENT_LECTURE_DETAIL = 0
        const val FRAGMENT_COLOR_PICKER = 1
        const val FRAGMENT_CUSTOM_DETAIL = 2

        // public final static int FRAGMENT_TEST = 2;
        const val FRAGMENT_ROOM_NUM = 3 // Number of fragments
    }
}
