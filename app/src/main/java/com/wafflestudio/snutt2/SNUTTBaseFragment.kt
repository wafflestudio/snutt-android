package com.wafflestudio.snutt2

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.common.base.Preconditions
import com.wafflestudio.snutt2.lib.rx.BindableFragment
import com.wafflestudio.snutt2.lib.rx.RxBinder
import com.wafflestudio.snutt2.ui.MainActivity

/**
 * Created by makesource on 2016. 1. 16..
 */
open class SNUTTBaseFragment : BindableFragment(), RxBinder {
    private var mActivity: Activity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mActivity = null
    }

    protected val app: SNUTTApplication
        protected get() = mActivity!!.application as SNUTTApplication
    val mainActivity: MainActivity?
        get() {
            val activity = mActivity
            Preconditions.checkState(activity is MainActivity)
            return activity as MainActivity?
        }
    val baseActivity: SNUTTBaseActivity?
        get() {
            val activity = mActivity
            Preconditions.checkState(activity is SNUTTBaseActivity)
            return activity as SNUTTBaseActivity?
        }

    protected fun hideSoftKeyboard(view: View) {
        if (mActivity == null) return
        val mgr = mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val TAG = "SNUTT_BASE_FRAGMENT"
        const val INTENT_KEY_TABLE_ID = "INTENT_KEY_TABLE_ID"
        const val INTENT_KEY_LECTURE_POSITION = "INTENT_KEY_LECTURE_POSITION"
    }
}
