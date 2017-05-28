package com.wafflestudio.snutt_staging.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.wafflestudio.snutt_staging.adapter.ColorListAdapter;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.Color;

import java.util.List;

/**
 * Created by makesource on 2016. 4. 5..
 */
public class ColorPickerFragment extends SNUTTBaseFragment {
    private static final String TAG = "COLOR_PICKER_FRAGMENT" ;

    private ListView listView;

    // Activity 로 데이터를 전달할 커스텀 리스너
    private ColorChangedListener mCallback;

    // Activity 로 데이터를 전달할 커스텀 리스너의 인터페이스
    public interface ColorChangedListener {
        public void onColorChanged(int index, Color color);
    }

    public static ColorPickerFragment newInstance() {
        return new ColorPickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_color_picker, container, false);
        final List<Color> colors = LectureManager.getInstance().getColorList();
        List<String> names = LectureManager.getInstance().getColorNameList();
        int index = getArguments().getInt("index");
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(new ColorListAdapter(colors, names, index));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == colors.size()) {
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
                                                    mCallback.onColorChanged(0, new Color(bgColor, fgColor));
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
                } else {
                    mCallback.onColorChanged(position + 1, null);
                    getActivity().onBackPressed();
                }
            }
        });
        //setDefaultColor();
        //setListener();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ColorChangedListener) activity;
    }

}
