package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment

/**
 * Created by makesource on 2017. 1. 24..
 */
class LicenseFragment : SNUTTBaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return inflater.inflate(R.layout.fragment_license, container, false)
    }
}