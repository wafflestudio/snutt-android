package com.wafflestudio.snutt.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
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

/**
 * Created by makesource on 2016. 4. 5..
 */
public class ColorPickerFragment extends SNUTTBaseFragment {

    private View fgColor1, bgColor1, layout1;
    private View fgColor2, bgColor2, layout2;
    private View fgColor3, bgColor3, layout3;
    private View fgColor4, bgColor4, layout4;
    private View fgColor5, bgColor5, layout5;
    private View fgColor6, bgColor6, layout6;
    private View fgColor7, bgColor7, layout7;



    public static ColorPickerFragment newInstance() {
        return new ColorPickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);

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



        setDefaultColor();
        layout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("배경색")
                        .initialColor(Color.RED)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);
                                ColorPickerDialogBuilder
                                        .with(getContext())
                                        .setTitle("텍스트색")
                                        .initialColor(Color.RED)
                                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                        .density(12)
                                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                                            @Override
                                            public void onColorSelected(int selectedColor) {
                                                Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setPositiveButton("ok", new ColorPickerClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                                //changeBackgroundColor(selectedColor);
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

        return rootView;
    }

    private void setDefaultColor() {
        fgColor1.setBackgroundColor(SNUTTUtils.getLectureColorByIndex(1));
        fgColor2.setBackgroundColor(SNUTTUtils.getLectureColorByIndex(2));
        fgColor3.setBackgroundColor(SNUTTUtils.getLectureColorByIndex(3));
        fgColor4.setBackgroundColor(SNUTTUtils.getLectureColorByIndex(4));
        fgColor5.setBackgroundColor(SNUTTUtils.getLectureColorByIndex(5));
        fgColor6.setBackgroundColor(SNUTTUtils.getLectureColorByIndex(6));
        bgColor1.setBackgroundColor(SNUTTUtils.getTextColorByIndex(1));
        bgColor2.setBackgroundColor(SNUTTUtils.getTextColorByIndex(2));
        bgColor3.setBackgroundColor(SNUTTUtils.getTextColorByIndex(3));
        bgColor4.setBackgroundColor(SNUTTUtils.getTextColorByIndex(4));
        bgColor5.setBackgroundColor(SNUTTUtils.getTextColorByIndex(5));
        bgColor6.setBackgroundColor(SNUTTUtils.getTextColorByIndex(6));
    }
}
