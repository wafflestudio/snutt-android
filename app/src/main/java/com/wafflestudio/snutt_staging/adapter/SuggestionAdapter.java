package com.wafflestudio.snutt_staging.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by makesource on 2017. 4. 9..
 */

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SUGGESTION_ADAPTER";

    private List<String> tagList;
    private List<String> filteredList;
    private static ClickListener clickListener;

    public SuggestionAdapter(List<String> lists) {
        this.tagList = lists;
        this.filteredList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    public String getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SuggestionViewHolder) holder).bindData(getItem(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        Log.d(TAG, "query : " + query);
        String constraintString = query.toLowerCase(Locale.ROOT);

        filteredList.clear();
        for (String tag : tagList) {
            if (tag.toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                filteredList.add(tag);
            }
        }
        notifyDataSetChanged();
    }


    protected static class SuggestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView suggestion;

        public SuggestionViewHolder(View itemView) {
            super(itemView);
            suggestion = (TextView) itemView.findViewById(R.id.suggestion_text);
            itemView.setOnClickListener(this);
        }

        private void bindData(String tag) {
            suggestion.setText(tag);
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

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}