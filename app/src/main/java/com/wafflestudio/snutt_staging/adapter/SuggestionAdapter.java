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
import com.wafflestudio.snutt_staging.manager.TagManager;
import com.wafflestudio.snutt_staging.model.Tag;
import com.wafflestudio.snutt_staging.model.TagType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by makesource on 2017. 4. 9..
 */

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "SUGGESTION_ADAPTER";
    private static ClickListener clickListener;

    private List<Tag> tagList;
    private List<Tag> filteredList;
    private boolean all, academicYear, category, classification, credit, department, instructor;
    private String query = "";

    public SuggestionAdapter(List<Tag> lists) {
        this.tagList = lists;
        this.filteredList = new ArrayList<>();
        this.all = true;
        this.academicYear = this.category = this.classification = this.credit = this.department = this.instructor = false;
        this.query = "";
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    public Tag getItem(int position) {
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
        this.query = query;
        this.all = !(academicYear || category || classification || credit || department || instructor);
        String constraintString = query.toLowerCase(Locale.ROOT);
        filteredList.clear();

        if (all) {
            for (Tag tag : tagList) {
                if (tag.getName().toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                    filteredList.add(tag);
                }
            }
        } else {
            for (Tag tag : tagList) {
                TagType type = tag.getTagType();
                if (type == null) continue;

                if (type == TagType.ACADEMIC_YEAR   && !academicYear) continue;
                if (type == TagType.CATEGORY        && !category) continue;
                if (type == TagType.CLASSIFICATION  && !classification) continue;
                if (type == TagType.CREDIT          && !credit) continue;
                if (type == TagType.DEPARTMENT      && !department) continue;
                if (type == TagType.INSTRUCTOR      && !instructor) continue;

                if (tag.getName().toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                    filteredList.add(tag);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean toggleAcademicYear() {
        academicYear = !academicYear;
        filter(query);
        return academicYear;
    }

    public boolean toggleCategory() {
        category = !category;
        filter(query);
        return category;
    }

    public boolean toggleClassification() {
        classification = !classification;
        filter(query);
        return classification;
    }

    public boolean toggleCredit() {
        credit = !credit;
        filter(query);
        return credit;
    }

    public boolean toggleDepartment() {
        department = !department;
        filter(query);
        return department;
    }

    public boolean toggleInstructor() {
        instructor = !instructor;
        filter(query);
        return instructor;
    }

    public void resetState() {
        query = "";
        all = true;
        academicYear = category = classification = credit = department = instructor = false;
    }

    protected static class SuggestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView suggestion;
        private Tag tag;

        public SuggestionViewHolder(View itemView) {
            super(itemView);
            suggestion = (TextView) itemView.findViewById(R.id.suggestion_text);
            itemView.setOnClickListener(this);
        }

        private void bindData(Tag tag) {
            this.tag = tag;
            suggestion.setText(tag.getName());
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v, tag);
            }
        }
    }

    public interface ClickListener {
        public void onClick(View v, Tag tag);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}