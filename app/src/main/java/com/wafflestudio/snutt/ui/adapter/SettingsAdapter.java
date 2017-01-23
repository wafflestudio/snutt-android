package com.wafflestudio.snutt.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.LectureItem;
import com.wafflestudio.snutt.model.SettingsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 11. 20..
 */

public class SettingsAdapter extends BaseAdapter {

    private Activity activity;
    private List<SettingsItem> lists;
    private LayoutInflater inflater;

    private final static int TYPE_HEADER = 0;
    private final static int TYPE_ITEM_TITLE = 1;

    public SettingsAdapter(Activity activity, List<SettingsItem> lists) {
        this.activity = activity;
        this.lists = lists;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public SettingsItem getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        SettingsItem item = getItem(position);
        return item.getType().getValue();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final SettingsItem item = getItem(position);
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEADER:
                view = inflater.inflate(R.layout.cell_header, viewGroup, false);
                break;
            case TYPE_ITEM_TITLE:
                view = inflater.inflate(R.layout.cell_settings, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.settings_text);
                textView.setText(item.getTitle());
                break;
        }
        return view;
    }
}
