package com.wafflestudio.snutt.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.LectureItem;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 9. 18..
 */
public class LectureCreateAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<LectureItem> lists;
    private LayoutInflater inflater;

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
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                value.setClickable(item.isEditable());
                value.setFocusable(item.isEditable());
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
                break;
            }
            case TYPE_ITEM_DETAIL: {
                TextInputLayout title1 = (TextInputLayout) view.findViewById(R.id.input_title1);
                EditText editText1 = (EditText) view.findViewById(R.id.input_detail1);
                title1.setHint(item.getTitle1());
                editText1.setText(item.getValue1());
                editText1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        item.setValue1(s.toString());
                    }
                });
                TextInputLayout title2 = (TextInputLayout) view.findViewById(R.id.input_title2);
                EditText editText2 = (EditText) view.findViewById(R.id.input_detail2);
                title2.setHint(item.getTitle2());
                editText2.setText(item.getValue2());
                editText2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        item.setValue2(s.toString());
                    }
                });
                editText1.setClickable(item.isEditable());
                editText1.setFocusable(item.isEditable());
                editText2.setClickable(item.isEditable());
                editText2.setFocusable(item.isEditable());
                if (position == 6) { // 학점
                    editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                break;
            }
            case TYPE_ITEM_BUTTON: {
                TextView textView = (TextView) view.findViewById(R.id.text_button);
                textView.setText("Add");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            }
        }

        return view;
    }
}
