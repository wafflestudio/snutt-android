package com.wafflestudio.snutt2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.adapter.IntroAdapter

/**
 * Created by makesource on 2016. 3. 18..
 */
class WelcomeActivity : SNUTTBaseActivity() {
    private val mViewPager: ViewPager? = null
    private val adapter: IntroAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityList.add(this)
        setContentView(R.layout.activity_welcome)
        val type = intent.getIntExtra(INTENT_KEY_FRAGMENT_TYPE, 0)
        when (type) {
            0 -> setFragment(SignInFragment.newInstance())
            1 -> setFragment(SignUpFragment.newInstance())
            else -> {
            }
        }
    }

    protected fun setFragment(fragment: Fragment?) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(android.R.id.content, fragment!!)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
    }
}
