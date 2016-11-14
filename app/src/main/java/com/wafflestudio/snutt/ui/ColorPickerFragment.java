package com.wafflestudio.snutt.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.SNUTTUtils;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Color;


import java.util.List;

/**
 * Created by makesource on 2016. 4. 5..
 */
public class ColorPickerFragment extends SNUTTBaseFragment {

    private List<Lecture> myLectures;
    private Lecture lecture;
    private View fgColor1, bgColor1, layout1;
    private View fgColor2, bgColor2, layout2;
    private View fgColor3, bgColor3, layout3;
    private View fgColor4, bgColor4, layout4;
    private View fgColor5, bgColor5, layout5;
    private View fgColor6, bgColor6, layout6;
    private View fgColor7, bgColor7, layout7;

    // Activity 로 데이터를 전달할 커스텀 리스너
    private ColorChangedListener mCallback;

    // Activity 로 데이터를 전달할 커스텀 리스너의 인터페이스
    public interface ColorChangedListener {
        public void onColorChanged(Color color);
    }

    public static ColorPickerFragment newInstance() {
        return new ColorPickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);

        Intent intent = getActivity().getIntent();
        int position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);

        fgColor1 = (View) rootView.findViewById(R.id.fgColor1);
        fgColor2 = (View) rootView.findViewById(R.id.fgColor2);
        fgColor3 = (View) rootView.findViewById(R.id.fgColor3);
        fgColor4 = (View) rootView.findViewById(R.id.fgColor4);
        fgColor5 = (View) rootView.findViewById(R.id.fgColor5);
        fgColor6 = (View) rootView.findViewById(R.id.fgColor6);
        fgColor7 = (View) rootView.findViewById(R.id.fgColor7);
        bgColor1 = (View) rootView.findViewById(R.id.bgColor1);
        bgColor2 = (View) rootView.findViewById(R.id.bgColor2);
        bgColor3 = (View) rootView.findViewById(R.id.bgColor3);
        bgColor4 = (View) rootView.findViewById(R.id.bgColor4);
        bgColor5 = (View) rootView.findViewById(R.id.bgColor5);
        bgColor6 = (View) rootView.findViewById(R.id.bgColor6);
        bgColor7 = (View) rootView.findViewById(R.id.bgColor7);
        layout1 = (LinearLayout) rootView.findViewById(R.id.color1);
        layout2 = (LinearLayout) rootView.findViewById(R.id.color2);
        layout3 = (LinearLayout) rootView.findViewById(R.id.color3);
        layout4 = (LinearLayout) rootView.findViewById(R.id.color4);
        layout5 = (LinearLayout) rootView.findViewById(R.id.color5);
        layout6 = (LinearLayout) rootView.findViewById(R.id.color6);
        layout7 = (LinearLayout) rootView.findViewById(R.id.color7);

        myLectures = LectureManager.getInstance().getLectures();
        lecture = myLectures.get(position);

        setDefaultColor();
        setListener();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ColorChangedListener) activity;
    }

    private void setDefaultColor() {
        bgColor1.setBackgroundColor(SNUTTUtils.getBgColorByIndex(1));
        bgColor2.setBackgroundColor(SNUTTUtils.getBgColorByIndex(2));
        bgColor3.setBackgroundColor(SNUTTUtils.getBgColorByIndex(3));
        bgColor4.setBackgroundColor(SNUTTUtils.getBgColorByIndex(4));
        bgColor5.setBackgroundColor(SNUTTUtils.getBgColorByIndex(5));
        bgColor6.setBackgroundColor(SNUTTUtils.getBgColorByIndex(6));
        bgColor7.setBackgroundColor(lecture.getBgColor());
        fgColor1.setBackgroundColor(SNUTTUtils.getFgColorByIndex(1));
        fgColor2.setBackgroundColor(SNUTTUtils.getFgColorByIndex(2));
        fgColor3.setBackgroundColor(SNUTTUtils.getFgColorByIndex(3));
        fgColor4.setBackgroundColor(SNUTTUtils.getFgColorByIndex(4));
        fgColor5.setBackgroundColor(SNUTTUtils.getFgColorByIndex(5));
        fgColor6.setBackgroundColor(SNUTTUtils.getFgColorByIndex(6));
        fgColor7.setBackgroundColor(lecture.getFgColor());
    }

    private void setListener() {
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(SNUTTUtils.getBgColorByIndex(1), SNUTTUtils.getFgColorByIndex(1)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(1), SNUTTUtils.getFgColorByIndex(1));
                getActivity().onBackPressed();
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(SNUTTUtils.getBgColorByIndex(2), SNUTTUtils.getFgColorByIndex(2)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(2), SNUTTUtils.getFgColorByIndex(2));
                getActivity().onBackPressed();
            }
        });
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(SNUTTUtils.getBgColorByIndex(3), SNUTTUtils.getFgColorByIndex(3)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(3), SNUTTUtils.getFgColorByIndex(3));
                getActivity().onBackPressed();
            }
        });
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(SNUTTUtils.getBgColorByIndex(4), SNUTTUtils.getFgColorByIndex(4)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(4), SNUTTUtils.getFgColorByIndex(4));
                getActivity().onBackPressed();
            }
        });
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(SNUTTUtils.getBgColorByIndex(5), SNUTTUtils.getFgColorByIndex(5)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(5), SNUTTUtils.getFgColorByIndex(5));
                getActivity().onBackPressed();
            }
        });
        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(SNUTTUtils.getBgColorByIndex(6), SNUTTUtils.getFgColorByIndex(6)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(6), SNUTTUtils.getFgColorByIndex(6));
                getActivity().onBackPressed();
            }
        });
        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("배경색")
                        .initialColor(lecture.getBgColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                final int bgColor = selectedColor;
                                ColorPickerDialogBuilder
                                        .with(getContext())
                                        .setTitle("텍스트색")
                                        .initialColor(lecture.getFgColor())
                                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                        .density(12)
                                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                                            @Override
                                            public void onColorSelected(int selectedColor) {
                                                //Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setPositiveButton("ok", new ColorPickerClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                                final int fgColor = selectedColor;
                                                mCallback.onColorChanged(new Color(bgColor, fgColor));
                                                //LectureManager.getInstance().updateLecture(lecture, bgColor, fgColor);
                                                getActivity().onBackPressed();
                                            }
                                        })
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .build()
                                        .show();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
    }
}
