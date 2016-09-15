package com.wafflestudio.snutt.ui.adapter;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.LectureItem;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureAdapter extends BaseAdapter {
    private ArrayList<LectureItem> lists;
    private LayoutInflater inflater;
    private Context context;

    private final static int TYPE_HEADER = 0;
    private final static int TYPE_ITEM_TITLE = 1;
    private final static int TYPE_ITEM_DETAIL = 2;
    private final static int TYPE_ITEM_BUTTON = 3;
    private final static int TYPE_ITEM_COLOR = 4;
    private final static int TYPE_ITEM_CLASS = 5;

    public LectureAdapter(Context context, ArrayList<LectureItem> lists) {
        this.context = context;
        this.lists = lists;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public int getItemViewType(int position) {
        LectureItem item = getItem(position);
        return item.getType().getValue();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LectureItem item = getItem(position);
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
                TextView value = (TextView) view.findViewById(R.id.text_value);
                title.setText(item.getTitle1());
                value.setText(item.getValue1());
                break;
            }
            case TYPE_ITEM_DETAIL: {
                TextInputLayout title1 = (TextInputLayout) view.findViewById(R.id.input_title1);
                EditText editText1 = (EditText) view.findViewById(R.id.input_detail1);
                title1.setHint(item.getTitle1());
                editText1.setText(item.getValue1());
                TextInputLayout title2 = (TextInputLayout) view.findViewById(R.id.input_title2);
                EditText editText2 = (EditText) view.findViewById(R.id.input_detail2);
                title2.setHint(item.getTitle2());
                editText2.setText(item.getValue2());
                if (item.isEditable()) {
                    editText1.setClickable(true);
                    editText1.setFocusable(true);
                    editText2.setClickable(true);
                    editText2.setFocusable(true);
                } else {
                    editText1.setClickable(false);
                    editText1.setFocusable(false);
                    editText2.setClickable(false);
                    editText2.setFocusable(false);
                }
                break;
            }
            case TYPE_ITEM_BUTTON: {
                TextView textView = (TextView) view.findViewById(R.id.text_button);
                textView.setText("Syllabus");
                break;
            }
            case TYPE_ITEM_COLOR: {
                TextView title = (TextView) view.findViewById(R.id.text_title);
                View fgColor = (View) view.findViewById(R.id.fgColor);
                View bgColor = (View) view.findViewById(R.id.bgColor);
                title.setText("색상");
                bgColor.setBackgroundColor(android.graphics.Color.parseColor(item.getColor().getBg()));
                fgColor.setBackgroundColor(android.graphics.Color.parseColor(item.getColor().getFg()));
                break;
            }
            case TYPE_ITEM_CLASS: {
                EditText editText1 = (EditText) view.findViewById(R.id.input_time);
                editText1.setHint("시간");
                editText1.setText(item.getValue1());
                editText1.setClickable(false);
                editText1.setFocusable(false);
                EditText editText2 = (EditText) view.findViewById(R.id.input_location);
                editText2.setHint("장소");
                editText2.setText(item.getValue2());
                editText2.setClickable(false);
                editText2.setFocusable(false);
                break;
            }
        }
        return view;
    }
}
