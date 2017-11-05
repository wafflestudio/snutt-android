package com.wafflestudio.snutt2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.manager.LectureManager;
import com.wafflestudio.snutt2.model.Color;

import java.util.List;

/**
 * Created by makesource on 2017. 5. 28..
 */

public class ColorListAdapter extends BaseAdapter {
    private static final String TAG = "COLOR_LIST_ADAPTER";
    private List<Color> colorList;
    private List<String> colorNameList;
    private int selected;

    public ColorListAdapter(List<Color> colors, List<String> colorNames, int index) {
        this.colorList = colors;
        this.colorNameList = colorNames;
        this.selected = (index == 0) ? colors.size() : index - 1;
    }

    @Override
    public int getCount() {
        return colorList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_color, parent, false);
        }
        TextView nameText = (TextView) v.findViewById(R.id.name);
        View fgColor = v.findViewById(R.id.fgColor);
        View bgColor = v.findViewById(R.id.bgColor);
        ImageView checked = (ImageView) v.findViewById(R.id.checked);
        checked.setVisibility(position == selected ? View.VISIBLE : View.INVISIBLE);

        if (position == colorList.size()) { // for custom color
            nameText.setText(LectureManager.getInstance().getDefaultColorName());
            fgColor.setBackgroundColor(LectureManager.getInstance().getDefaultFgColor());
            bgColor.setBackgroundColor(LectureManager.getInstance().getDefaultBgColor());
        } else {
            Color color = colorList.get(position);
            String name = colorNameList.get(position);
            nameText.setText(name);
            fgColor.setBackgroundColor(color.getFg());
            bgColor.setBackgroundColor(color.getBg());
        }
        return v;
    }
}
