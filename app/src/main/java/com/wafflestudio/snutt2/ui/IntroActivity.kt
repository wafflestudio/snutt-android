package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.viewpagerindicator.CirclePageIndicator
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.adapter.IntroPagerAdapter

/**
 * Created by makesource on 2017. 6. 22..
 */
class IntroActivity : SNUTTBaseActivity() {
    private var mIntroPagerAdapter: IntroPagerAdapter? = null
    private var mViewPager: ViewPager? = null
    private var mIndicator: CirclePageIndicator? = null
    private var signIn: Button? = null
    private var signUp: Button? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        activityList.add(this)
        setContentView(R.layout.activity_intro)
        mIntroPagerAdapter = IntroPagerAdapter(supportFragmentManager)
        mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager!!.adapter = mIntroPagerAdapter
        mIndicator = findViewById<View>(R.id.indicator) as CirclePageIndicator
        mIndicator!!.setViewPager(mViewPager)
        signIn = findViewById<View>(R.id.sign_in) as Button
        signUp = findViewById<View>(R.id.sign_up) as Button
        signIn!!.setOnClickListener { startWelcome(0) }
        signUp!!.setOnClickListener { startWelcome(1) }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityList.remove(this)
    }
}
