package com.wafflestudio.snutt.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTUtils;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.Lecture;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2017. 3. 7..
 */

public class LectureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum VIEW_TYPE {
        Lecture(0),
        ProgressBar(1);
        private final int value;
        VIEW_TYPE(int value) {
            this.value = value;
        }
        public final int getValue() {
            return value;
        }
    }

    private static final String TAG = "LECTURE_LIST_ADAPTER" ;
    private static ClickListener clickListener;

    private List<Lecture> lectures;

    public LectureListAdapter(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE.Lecture.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture, parent, false);
            // create ViewHolder
            return new LectureViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_progressbar, parent, false);
            return new ProgressBarViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        int itemType = getItemViewType(position);
        if (itemType == VIEW_TYPE.ProgressBar.getValue()) return;

        final Lecture lecture = lectures.get(position);
        final LectureViewHolder holder = (LectureViewHolder) viewHolder;
        final int selectedPosition = LectureManager.getInstance().getSelectedPosition();
        holder.bindData(lecture);

        if (selectedPosition == position) {
            holder.layout.setBackgroundColor(Color.parseColor("#33000000"));
            if (LectureManager.getInstance().alreadyOwned(lecture)) {
                holder.add.setVisibility(View.GONE);
                holder.margin.setVisibility(View.GONE);
                holder.remove.setVisibility(View.VISIBLE);
            } else {
                holder.remove.setVisibility(View.GONE);
                holder.margin.setVisibility(View.GONE);
                holder.add.setVisibility(View.VISIBLE);
            }
        } else {
            holder.add.setVisibility(View.GONE);
            holder.remove.setVisibility(View.GONE);
            holder.margin.setVisibility(View.VISIBLE);
            holder.layout.setBackgroundColor(Color.TRANSPARENT);
        }

        // 3개의 서로 다른 클릭을 구분하기 위해 onBindViewHolder 에서 view Id 를 비교함.
        setOnItemClickListener(new ClickListener() {
            @Override
            public void onClick(View v, final int position) {
                if (v.getId() == holder.layout.getId()) {
                    Log.d(TAG, "View ID : " + v.getId());
                    Log.d(TAG, String.valueOf(position) + " item Clicked!!");
                    if (selectedPosition == position) {
                        notifyItemChanged(position);
                        LectureManager.getInstance().setSelectedLecture(null);
                    } else {
                        notifyItemChanged(selectedPosition);
                        notifyItemChanged(position);
                        LectureManager.getInstance().setSelectedLecture(getItem(position));
                    }

                } else if (v.getId() == holder.add.getId()) {
                    Log.d(TAG, "View ID : " + v.getId());
                    Log.d(TAG, String.valueOf(position) + " add Clicked!!");
                    LectureManager.getInstance().addLecture(getItem(position), new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            notifyItemChanged(position);
                        }
                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                } else {
                    Log.d(TAG, "View ID : " + v.getId());
                    Log.d(TAG, String.valueOf(position) + " remove Clicked!!");
                    LectureManager.getInstance().removeLecture(getItem(position), new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            notifyItemChanged(position);
                        }
                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Lecture item = lectures.get(position);
        return (item == null) ? VIEW_TYPE.ProgressBar.getValue() : VIEW_TYPE.Lecture.getValue();
    }

    @Override
    public int getItemCount() {
        return lectures.size();
    }

    public Lecture getItem(int position) {
        return lectures.get(position);
    }

    private static class LectureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected View layout;
        protected TextView title;
        protected TextView subTitle;
        protected TextView tag;
        protected TextView classTime;
        protected TextView location;
        protected Button add;
        protected Button remove;
        protected View margin;

        private LectureViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title);
            tag = (TextView) itemView.findViewById(R.id.tag);
            classTime = (TextView) itemView.findViewById(R.id.time);
            location = (TextView) itemView.findViewById(R.id.location);
            add = (Button) itemView.findViewById(R.id.add);
            remove = (Button) itemView.findViewById(R.id.remove);
            margin = itemView.findViewById(R.id.margin);
            this.layout.setOnClickListener(this);
            this.add.setOnClickListener(this);
            this.remove.setOnClickListener(this);
        }

        private void bindData(Lecture lecture) {
            int selectedPosition = LectureManager.getInstance().getSelectedPosition();
            String titleText = lecture.getCourse_title();
            String subTitleText =  "(" + lecture.getInstructor() + " / " + String.valueOf(lecture.getCredit()) + "학점)";
            title.setText(titleText);
            title.setTextScaleX(1.0f);
            subTitle.setText(subTitleText);

            boolean selected = (getPosition() == selectedPosition);
            int margin = 20 + 20 + 10 + (selected ? 80 : 0);
            int maxWidth = (int) (SNUTTUtils.getDisplayWidth() - SNUTTUtils.dpTopx(margin));
            int subTitleWidth = (int) Math.min(getTextViewWidth(subTitle), maxWidth / 2);
            int titleWidth = (int) Math.min(getTextViewWidth(title), maxWidth - subTitleWidth);
            if (titleWidth + subTitleWidth < maxWidth) {
                subTitleWidth = maxWidth - titleWidth;
            }

            title.setLayoutParams(new LinearLayout.LayoutParams(titleWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            title.setSelected(selected);
            title.setEllipsize(selected ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
            title.setTextScaleX(1.0f);
            subTitle.setLayoutParams(new LinearLayout.LayoutParams(subTitleWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            String tagText = "";
            if (!Strings.isNullOrEmpty(lecture.getCategory())) {
                tagText += lecture.getCategory() + ", ";
            }
            if (!Strings.isNullOrEmpty(lecture.getDepartment())) {
                tagText += lecture.getDepartment() + ", ";
            }
            tagText += lecture.getAcademic_year();
            if (Strings.isNullOrEmpty(tagText)) tagText = "(없음)";

            if (selected) {
                if (!Strings.isNullOrEmpty(lecture.getRemark())) {
                    tag.setText(lecture.getRemark());
                } else {
                    tag.setText(tagText);
                }
                tag.setSelected(true);
                tag.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            } else {
                tag.setText(tagText);
                tag.setSelected(false);
                tag.setEllipsize(TextUtils.TruncateAt.END);
            }

            String classTimeText = lecture.getSimplifiedClassTime();
            if (Strings.isNullOrEmpty(classTimeText)) classTimeText = "(없음)";
            classTime.setText(classTimeText);

            String locationText = lecture.getSimplifiedLocation();
            if (Strings.isNullOrEmpty(locationText)) locationText = "(없음)";
            location.setText(locationText);
        }

        private float getTextViewWidth(TextView textView) {
            textView.measure(0, 0);
            return textView.getMeasuredWidth();
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v, getPosition());
            }
        }
    }

    private static class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        private ProgressBarViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    private interface ClickListener {
        public void onClick(View v, int position);
    }

    private void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
