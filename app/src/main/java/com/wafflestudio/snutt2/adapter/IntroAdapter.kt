package com.wafflestudio.snutt2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.wafflestudio.snutt2.ui.SignInFragment
import com.wafflestudio.snutt2.ui.SignUpFragment

/**
 * Created by makesource on 2016. 3. 26..
 */
class IntroAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                if (signInFragment == null) signInFragment = SignInFragment.newInstance()
                signInFragment!!
            }
            1 -> {
                if (signUpFragment == null) signUpFragment = SignUpFragment.newInstance()
                signUpFragment!!
            }
            else -> {
                throw IllegalStateException("intro fragment position is out of index!")
            }
        }
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "로그인"
            1 -> return "회원가입"
        }
        return null
    }

    companion object {
        private const val TAG = "INTRO_ADAPTER"
        private const val NUM_ITEMS = 2
        private var signInFragment: SignInFragment? = null
        private var signUpFragment: SignUpFragment? = null
    }
}