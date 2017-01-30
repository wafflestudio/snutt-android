package com.wafflestudio.snutt.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.SettingsItem;

import java.util.List;

/**
 * Created by makesource on 2016. 11. 21..
 */

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ClickListener clickListener;
    private List<SettingsItem> lists;
    private LayoutInflater inflater;

    private final static int TYPE_HEADER = 0;
    private final static int TYPE_TITLE = 1;

    public SettingsAdapter(Activity activity, List<SettingsItem> lists) {
        this.lists = lists;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.cell_header, null);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.cell_settings, null);
            return new TitleViewHolder(view); // view holder for header items
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if (itemType == TYPE_TITLE) {
            ((TitleViewHolder)holder).bindData(getItem(position));
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        SettingsItem item = getItem(position);
        return item.getViewType().getValue();
    }

    public SettingsItem getItem(int position) {
        return lists.get(position);
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        public HeaderViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.cell_header);
            layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30));
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private View view;
        private TextView title;
        private TextView detail;

        public TitleViewHolder(View view) {
            super(view);
            this.view = view;
            this.title = (TextView) view.findViewById(R.id.settings_text);
            this.detail = (TextView) view.findViewById(R.id.settings_detail);
            this.view.setOnClickListener(this);
        }

        public void bindData(SettingsItem item) {
            title.setText(item.getTitle());
            detail.setText(item.getDetail());
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v,getPosition());
            }
        }
    }

    public interface ClickListener {
        public void onClick(View v, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}


