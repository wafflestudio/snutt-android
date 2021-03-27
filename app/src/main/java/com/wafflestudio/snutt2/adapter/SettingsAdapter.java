package com.wafflestudio.snutt2.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.model.SettingsItem;

import java.util.List;

/**
 * Created by makesource on 2016. 11. 21..
 */

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ClickListener clickListener;
    private List<SettingsItem> lists;

    public SettingsAdapter(List<SettingsItem> lists) {
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SettingsItem.ViewType.Header.getValue()) {
            View view = inflater.inflate(R.layout.cell_setting_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.cell_settings, parent, false);
            return new TitleViewHolder(view); // view holder for header items
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if (itemType == SettingsItem.ViewType.ItemTitle.getValue()) {
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
            //layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private View view;
        private TextView title;
        private TextView detail;
        private ImageView arrow;

        private TitleViewHolder(View view) {
            super(view);
            this.view = view;
            this.title = (TextView) view.findViewById(R.id.settings_text);
            this.detail = (TextView) view.findViewById(R.id.settings_detail);
            this.arrow = (ImageView) view.findViewById(R.id.settings_arrow);
            this.view.setOnClickListener(this);
        }

        private void bindData(SettingsItem item) {
            title.setText(item.getTitle());
            detail.setText(item.getDetail());

            switch (item.getType()) {
                case AddIdPassword:
                case ChangePassword:
                case LinkFacebook:
                case DeleteFacebook:
                case ChangeEmail:
                case Leave:
                    arrow.setVisibility(View.VISIBLE);
                    break;
                default:
                    arrow.setVisibility(View.GONE);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v, getPosition());
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


