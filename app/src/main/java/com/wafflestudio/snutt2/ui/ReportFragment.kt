package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import com.google.common.base.Strings
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.manager.UserManager.Companion.instance
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 * Created by makesource on 2017. 1. 24..
 */
class ReportFragment : SNUTTBaseFragment() {
    private var emailText: EditText? = null
    private var detailText: EditText? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_report, container, false)
        setHasOptionsMenu(true)
        emailText = rootView.findViewById<View>(R.id.email_editText) as EditText
        detailText = rootView.findViewById<View>(R.id.detail_editText) as EditText
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_report, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_send) {
            if (Strings.isNullOrEmpty(detailText!!.text.toString())) {
                Toast.makeText(app, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                val email = emailText!!.text.toString()
                val detail = detailText!!.text.toString()
                item.isEnabled = false
                instance!!.postFeedback(email, detail, object : Callback<Any> {
                    override fun success(o: Any?, response: Response) {
                        Toast.makeText(app, "전송하였습니다", Toast.LENGTH_SHORT).show()
                        activity!!.finish()
                    }

                    override fun failure(error: RetrofitError) {
                        item.isEnabled = true
                    }
                })
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}