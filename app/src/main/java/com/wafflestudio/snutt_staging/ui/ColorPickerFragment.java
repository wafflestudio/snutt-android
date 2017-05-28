package com.wafflestudio.snutt_staging.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.common.base.Preconditions;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.Color;

/**
 * Created by makesource on 2016. 4. 5..
 */
public class ColorPickerFragment extends SNUTTBaseFragment {
    private static final String TAG = "COLOR_PICKER_FRAGMENT" ;

    private ListView listView;
    private View fgColor1, bgColor1, layout1;
    private View fgColor2, bgColor2, layout2;
    private View fgColor3, bgColor3, layout3;
    private View fgColor4, bgColor4, layout4;
    private View fgColor5, bgColor5, layout5;
    private View fgColor6, bgColor6, layout6;
    private View fgColor7, bgColor7, layout7;
    private View fgColor8, bgColor8, layout8;
    private View fgColor9, bgColor9, layout9;
    private View fgColor10, bgColor10, layout10;
    private TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8, textView9, textView10;

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
        listView = (ListView) rootView.findViewById(R.id.listView);
        fgColor1 = (View) rootView.findViewById(R.id.fgColor1);
        fgColor2 = (View) rootView.findViewById(R.id.fgColor2);
        fgColor3 = (View) rootView.findViewById(R.id.fgColor3);
        fgColor4 = (View) rootView.findViewById(R.id.fgColor4);
        fgColor5 = (View) rootView.findViewById(R.id.fgColor5);
        fgColor6 = (View) rootView.findViewById(R.id.fgColor6);
        fgColor7 = (View) rootView.findViewById(R.id.fgColor7);
        fgColor8 = (View) rootView.findViewById(R.id.fgColor8);
        fgColor9 = (View) rootView.findViewById(R.id.fgColor9);
        fgColor10 = (View) rootView.findViewById(R.id.fgColor10);
        bgColor1 = (View) rootView.findViewById(R.id.bgColor1);
        bgColor2 = (View) rootView.findViewById(R.id.bgColor2);
        bgColor3 = (View) rootView.findViewById(R.id.bgColor3);
        bgColor4 = (View) rootView.findViewById(R.id.bgColor4);
        bgColor5 = (View) rootView.findViewById(R.id.bgColor5);
        bgColor6 = (View) rootView.findViewById(R.id.bgColor6);
        bgColor7 = (View) rootView.findViewById(R.id.bgColor7);
        bgColor8 = (View) rootView.findViewById(R.id.bgColor8);
        bgColor9 = (View) rootView.findViewById(R.id.bgColor9);
        bgColor10 = (View) rootView.findViewById(R.id.bgColor10);
        layout1 = (LinearLayout) rootView.findViewById(R.id.color1);
        layout2 = (LinearLayout) rootView.findViewById(R.id.color2);
        layout3 = (LinearLayout) rootView.findViewById(R.id.color3);
        layout4 = (LinearLayout) rootView.findViewById(R.id.color4);
        layout5 = (LinearLayout) rootView.findViewById(R.id.color5);
        layout6 = (LinearLayout) rootView.findViewById(R.id.color6);
        layout7 = (LinearLayout) rootView.findViewById(R.id.color7);
        layout8 = (LinearLayout) rootView.findViewById(R.id.color8);
        layout9 = (LinearLayout) rootView.findViewById(R.id.color9);
        layout10 = (LinearLayout) rootView.findViewById(R.id.color10);
        textView1 = (TextView) rootView.findViewById(R.id.name1);
        textView2 = (TextView) rootView.findViewById(R.id.name2);
        textView3 = (TextView) rootView.findViewById(R.id.name3);
        textView4 = (TextView) rootView.findViewById(R.id.name4);
        textView5 = (TextView) rootView.findViewById(R.id.name5);
        textView6 = (TextView) rootView.findViewById(R.id.name6);
        textView7 = (TextView) rootView.findViewById(R.id.name7);
        textView8 = (TextView) rootView.findViewById(R.id.name8);
        textView9 = (TextView) rootView.findViewById(R.id.name9);
        textView10 = (TextView) rootView.findViewById(R.id.name10);

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
        bgColor1.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(1));
        bgColor2.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(2));
        bgColor3.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(3));
        bgColor4.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(4));
        bgColor5.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(5));
        bgColor6.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(6));
        bgColor7.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(7));
        bgColor8.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(8));
        bgColor9.setBackgroundColor(LectureManager.getInstance().getBgColorByIndex(9));
        bgColor10.setBackgroundColor(LectureManager.getInstance().getDefaultBgColor());
        fgColor1.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(1));
        fgColor2.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(2));
        fgColor3.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(3));
        fgColor4.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(4));
        fgColor5.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(5));
        fgColor6.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(6));
        fgColor7.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(7));
        fgColor8.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(8));
        fgColor9.setBackgroundColor(LectureManager.getInstance().getFgColorByIndex(9));
        fgColor10.setBackgroundColor(LectureManager.getInstance().getDefaultFgColor());
        textView1.setText(LectureManager.getInstance().getColorNameByIndex(1));
        textView2.setText(LectureManager.getInstance().getColorNameByIndex(2));
        textView3.setText(LectureManager.getInstance().getColorNameByIndex(3));
        textView4.setText(LectureManager.getInstance().getColorNameByIndex(4));
        textView5.setText(LectureManager.getInstance().getColorNameByIndex(5));
        textView6.setText(LectureManager.getInstance().getColorNameByIndex(6));
        textView7.setText(LectureManager.getInstance().getColorNameByIndex(7));
        textView8.setText(LectureManager.getInstance().getColorNameByIndex(8));
        textView9.setText(LectureManager.getInstance().getColorNameByIndex(9));
        textView10.setText(LectureManager.getInstance().getDefaultColorName());
    }

    private void setListener() {
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(1), LectureManager.getInstance().getFgColorByIndex(1)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(1), SNUTTUtils.getFgColorByIndex(1));
                getActivity().onBackPressed();
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(2), LectureManager.getInstance().getFgColorByIndex(2)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(2), SNUTTUtils.getFgColorByIndex(2));
                getActivity().onBackPressed();
            }
        });
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(3), LectureManager.getInstance().getFgColorByIndex(3)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(3), SNUTTUtils.getFgColorByIndex(3));
                getActivity().onBackPressed();
            }
        });
        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(4), LectureManager.getInstance().getFgColorByIndex(4)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(4), SNUTTUtils.getFgColorByIndex(4));
                getActivity().onBackPressed();
            }
        });
        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(5), LectureManager.getInstance().getFgColorByIndex(5)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(5), SNUTTUtils.getFgColorByIndex(5));
                getActivity().onBackPressed();
            }
        });
        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(6), LectureManager.getInstance().getFgColorByIndex(6)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(6), SNUTTUtils.getFgColorByIndex(6));
                getActivity().onBackPressed();
            }
        });
        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(7), LectureManager.getInstance().getFgColorByIndex(7)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(6), SNUTTUtils.getFgColorByIndex(6));
                getActivity().onBackPressed();
            }
        });
        layout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(8), LectureManager.getInstance().getFgColorByIndex(8)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(6), SNUTTUtils.getFgColorByIndex(6));
                getActivity().onBackPressed();
            }
        });
        layout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onColorChanged(new Color(LectureManager.getInstance().getBgColorByIndex(9), LectureManager.getInstance().getFgColorByIndex(9)));
                //LectureManager.getInstance().updateLecture(lecture, SNUTTUtils.getBgColorByIndex(6), SNUTTUtils.getFgColorByIndex(6));
                getActivity().onBackPressed();
            }
        });
        layout10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("배경색")
                        .initialColor(LectureManager.getInstance().getDefaultBgColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Log.d(TAG, "onColorSelected: 0x" + Integer.toHexString(selectedColor));
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
                                        .initialColor(LectureManager.getInstance().getDefaultFgColor())
                                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                        .density(12)
                                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                                            @Override
                                            public void onColorSelected(int selectedColor) {
                                                Log.d(TAG, "onColorSelected: 0x" + Integer.toHexString(selectedColor));
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

    private LectureMainActivity getLectureMainActivity() {
        Activity activity = getActivity();
        Preconditions.checkArgument(activity instanceof LectureMainActivity);
        return (LectureMainActivity) activity;
    }
}
