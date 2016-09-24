package com.wafflestudio.snutt.ui.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.ClassTime;
import com.wafflestudio.snutt.model.LectureItem;
import com.wafflestudio.snutt.ui.LectureMainActivity;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 9. 18..
 */
public class LectureCreateAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<LectureItem> lists;
    private LayoutInflater inflater;
    private boolean isAnimated = true;

    private final static String TAG = "LECTURE_CREATE_ADAPTER";
    private final static int TYPE_HEADER = 0;
    private final static int TYPE_ITEM_TITLE = 1;
    private final static int TYPE_ITEM_DETAIL = 2;
    private final static int TYPE_ITEM_BUTTON = 3;
    private final static int TYPE_ITEM_COLOR = 4;
    private final static int TYPE_ITEM_CLASS = 5;

    public LectureCreateAdapter(Activity activity, ArrayList<LectureItem> lists) {
        this.activity = activity;
        this.lists = lists;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        LectureItem item = getItem(position);
        return item.getType().getValue();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public LectureItem getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final LectureItem item = getItem(position);
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEADER:
                view = inflater.inflate(R.layout.cell_lecture_header, viewGroup, false);
                break;
            case TYPE_ITEM_TITLE:
                view = inflater.inflate(R.layout.cell_lecture_item_title, viewGroup, false);
                break;
            case TYPE_ITEM_DETAIL:
                view = inflater.inflate(R.layout.cell_lecture_item_detail, viewGroup, false);
                break;
            case TYPE_ITEM_BUTTON:
                view = inflater.inflate(R.layout.cell_lecture_item_button, viewGroup, false);
                break;
            case TYPE_ITEM_COLOR:
                view = inflater.inflate(R.layout.cell_lecture_item_color, viewGroup, false);
                break;
            case TYPE_ITEM_CLASS:
                view = inflater.inflate(R.layout.cell_lecture_item_class, viewGroup, false);
                break;
        }
        switch (type) {
            case TYPE_ITEM_TITLE: {
                TextView title = (TextView) view.findViewById(R.id.text_title);
                EditText value = (EditText) view.findViewById(R.id.text_value);
                title.setText(item.getTitle1());
                value.setText(item.getValue1());
                value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        item.setValue1(s.toString());
                    }
                });
                if (position == 4) { // 학점
                    value.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                break;
            }
            case TYPE_ITEM_BUTTON: {
                TextView textView = (TextView) view.findViewById(R.id.text_button);
                textView.setText("Add");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addClassItem();
                        isAnimated = false;
                        notifyDataSetChanged();
                    }
                });
                break;
            }
            case TYPE_ITEM_COLOR: {
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
                TextView title = (TextView) view.findViewById(R.id.text_title);
                View fgColor = (View) view.findViewById(R.id.fgColor);
                View bgColor = (View) view.findViewById(R.id.bgColor);
                title.setText("색상");
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isEditable()) {
                            ((LectureMainActivity) activity).setColorPickerFragment();
                        }
                    }
                });
                bgColor.setBackgroundColor(item.getColor().getBg());
                fgColor.setBackgroundColor(item.getColor().getFg());
                break;
            }
            case TYPE_ITEM_CLASS: {
                final EditText editText1 = (EditText) view.findViewById(R.id.input_time);
                final EditText editText2 = (EditText) view.findViewById(R.id.input_location);
                if (position == getCount() - 2) {
                    if (!isAnimated) {
                        editText1.setVisibility(View.GONE);
                        editText2.setVisibility(View.GONE);
                        final DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                        ValueAnimator va = ValueAnimator.ofInt(0, (int)(60 * dm.density));
                        final View finalView = view;
                        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (Integer) animation.getAnimatedValue();
                                finalView.getLayoutParams().height = value;
                                finalView.requestLayout();
                                if (value == (int) (60 * dm.density)) {
                                    editText1.setVisibility(View.VISIBLE);
                                    editText2.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        va.setDuration(400);
                        va.start();
                        isAnimated = true;
                    }
                }
            }
        }
        return view;
    }

    private void addClassItem() {
        int pos = getCount() - 1;
        lists.add(pos, new LectureItem(new ClassTime(1,1,1," "), LectureItem.Type.ItemClass));
    }
}
