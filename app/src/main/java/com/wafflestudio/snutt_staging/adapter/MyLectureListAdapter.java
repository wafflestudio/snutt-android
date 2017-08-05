package com.wafflestudio.snutt_staging.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 2. 23..
 */
public class MyLectureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MY_LECTURE_LIST_ADAPTER";
    private static ClickListener clickListener;
    private static LongClickListener longClickListener;
    private List<Lecture> myLecture;

    public MyLectureListAdapter(List<Lecture> myLecture) {
        this.myLecture = myLecture;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellLayoutView;
        ViewHolder viewHolder;

        cellLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_my_lecture, parent, false);
        // create ViewHolder
        viewHolder = new ViewHolder(cellLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Lecture lecture = myLecture.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.bindData(lecture);
    }

    @Override
    public int getItemCount() {
        if (myLecture != null) {
            return myLecture.size();
        }
        return 0;
    }

    // inner class to hold a reference to each item of RecyclerView
    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private View layout;
        private TextView title;
        private TextView subTitle;
        private TextView tag;
        private TextView classTime;
        private TextView location;
        private LinearLayout titleLayout;
        private LinearLayout subTitleLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title);
            tag = (TextView) itemView.findViewById(R.id.tag);
            classTime = (TextView) itemView.findViewById(R.id.time);
            location = (TextView) itemView.findViewById(R.id.location);
            titleLayout = (LinearLayout) itemView.findViewById(R.id.title_layout);
            subTitleLayout = (LinearLayout) itemView.findViewById(R.id.sub_title_layout);
            this.layout.setOnClickListener(this);
            this.layout.setOnLongClickListener(this);
        }

        private void bindData(Lecture lecture) {
            String titleText = lecture.getCourse_title();
            String subTitleText =  "(" + lecture.getInstructor() + " / " + String.valueOf(lecture.getCredit()) + "학점)";
            title.setText(titleText);
            subTitle.setText(subTitleText);
            titleLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            subTitleLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            titleLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (titleLayout.getWidth() > title.getWidth()) {
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(title.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, 0f);
                        titleLayout.setLayoutParams(param);
                    }
                }
            });

            subTitleLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (subTitleLayout.getWidth() > subTitle.getWidth()) {
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(subTitle.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, 0f);
                        subTitleLayout.setLayoutParams(param);
                    }
                }
            });

            String tagText = "";
            if (!Strings.isNullOrEmpty(lecture.getCategory())) {
                tagText += lecture.getCategory() + ", ";
            }
            if (!Strings.isNullOrEmpty(lecture.getDepartment())) {
                tagText += lecture.getDepartment() + ", ";
            }
            if (!Strings.isNullOrEmpty(lecture.getAcademic_year())) {
                tagText += lecture.getAcademic_year();
            }
            if (Strings.isNullOrEmpty(tagText)) tagText = "(없음)";
            tag.setText(tagText);

            String classTimeText = lecture.getSimplifiedClassTime();
            if (Strings.isNullOrEmpty(classTimeText)) classTimeText = "(없음)";
            classTime.setText(classTimeText);

            String locationText = lecture.getSimplifiedLocation();
            if (Strings.isNullOrEmpty(locationText)) locationText = "(없음)";
            location.setText(locationText);


        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v,getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                longClickListener.onLongClick(v, getPosition());
            }
            return true;
        }
    }

    public interface ClickListener {
        public void onClick(View v, int position);
    }

    public interface LongClickListener {
        public void onLongClick(View v, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnItemLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
