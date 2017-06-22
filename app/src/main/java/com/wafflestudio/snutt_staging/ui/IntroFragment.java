package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;

/**
 * Created by makesource on 2017. 6. 23..
 */

public class IntroFragment extends SNUTTBaseFragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ImageView title;
    private ImageView detail;

    public static IntroFragment newInstance(int sectionNumber) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
        int section = getArguments().getInt(ARG_SECTION_NUMBER);
        title = (ImageView) rootView.findViewById(R.id.intro_title);
        detail = (ImageView) rootView.findViewById(R.id.intro_detail);

        switch (section) {
            case 0:
                title.setImageResource(R.drawable.imgintrotitle1);
                detail.setImageResource(R.drawable.imgintro1);
                break;
            case 1:
                title.setImageResource(R.drawable.imgintrotitle2);
                detail.setImageResource(R.drawable.imgintro2);
                break;
            case 2:
                title.setImageResource(R.drawable.imgintrotitle3);
                detail.setImageResource(R.drawable.imgintro3);
                break;
            default:
                break;
        }

        return rootView;
    }
}
