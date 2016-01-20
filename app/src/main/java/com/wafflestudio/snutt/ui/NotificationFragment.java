package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class NotificationFragment extends SNUTTBaseFragment { /**
 * The fragment argument representing the section number for this
 * fragment.
 */
private static final String ARG_SECTION_NUMBER = "section_number";

    public NotificationFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotificationFragment newInstance(int sectionNumber) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}
