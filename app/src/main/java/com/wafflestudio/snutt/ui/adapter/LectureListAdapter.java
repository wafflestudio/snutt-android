package com.wafflestudio.snutt.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 2. 10..
 */
public class LectureListAdapter extends RecyclerView.Adapter<LectureListAdapter.ViewHolder> {

    private static final String TAG = "LECTURE_LIST_ADAPTER" ;

    private List<Lecture> lectures;
    private int selectedPosition = -1;

    public LectureListAdapter(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cellLayoutView;
        ViewHolder viewHolder;

        cellLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_lecture, parent, false);
        // create ViewHolder
        viewHolder = new ViewHolder(cellLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Lecture lecture = lectures.get(position);

        holder.courseTitle.setText(lecture.getCourse_title());
        holder.courseNumber.setText(lecture.getCourse_number());
        holder.lectureNumber.setText(lecture.getLecture_number());
        holder.classification.setText(lecture.getClassification());
        holder.department.setText(lecture.getDepartment());
        holder.classTime.setText(lecture.getClass_time());
        holder.location.setText(lecture.getLocation());
        holder.remark.setText(lecture.getRemark());

        if (selectedPosition == position) {
            holder.lectureLayout.setBackgroundColor(Color.GRAY);
            if (LectureManager.getInstance().alreadyOwned(lecture)) {
                holder.add.setVisibility(View.GONE);
                holder.remove.setVisibility(View.VISIBLE);
            } else {
                holder.add.setVisibility(View.VISIBLE);
                holder.remove.setVisibility(View.GONE);
            }

        } else {
            holder.add.setVisibility(View.GONE);
            holder.remove.setVisibility(View.GONE);
            holder.lectureLayout.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.setClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                //TODO : (Seongowon) 배경색 바꾸기 등등 시각적 효과 넣기
                //TODO : (Seongowon) 내 강의 리스트와 비교해서 이미 있는 강의면 remove를 없으면 add버튼을 활성화
                if (v.getId() == holder.lectureLayout.getId()) {
                    Log.d(TAG, String.valueOf(position) + " item Clicked!!");
                    if (selectedPosition == position) {
                        selectedPosition = -1;
                        notifyItemChanged(position);
                        LectureManager.getInstance().setSelectedLecture(null);
                    } else {
                        notifyItemChanged(selectedPosition);
                        notifyItemChanged(position);
                        selectedPosition = position;
                        LectureManager.getInstance().setSelectedLecture(lecture);
                    }

                } else if (v.getId() == holder.add.getId()) {
                    Log.d(TAG, String.valueOf(position) + " add Clicked!!");
                    LectureManager.getInstance().addLecture(lecture);
                    notifyItemChanged(position);
                } else {
                    Log.d(TAG, String.valueOf(position) + " remove Clicked!!");
                    LectureManager.getInstance().removeLecture(lecture);
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lectures.size();
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
        notifyDataSetChanged();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View lectureLayout;
        public TextView courseTitle;
        public TextView courseNumber;
        public TextView lectureNumber;
        public TextView classification;
        public TextView department;
        public TextView classTime;
        public TextView location;
        public TextView remark;

        public Button add;
        public Button remove;

        private ClickListener clickListener;

        public ViewHolder(View view) {
            super(view);
            this.lectureLayout = view;
            this.courseTitle = (TextView) view.findViewById(R.id.course_title);
            this.courseNumber = (TextView) view.findViewById(R.id.course_number);
            this.lectureNumber = (TextView) view.findViewById(R.id.lecture_number);
            this.classification = (TextView) view.findViewById(R.id.classification);
            this.department = (TextView) view.findViewById(R.id.department);
            this.classTime = (TextView) view.findViewById(R.id.class_time);
            this.location = (TextView) view.findViewById(R.id.location);
            this.remark = (TextView) view.findViewById(R.id.remark);

            this.add = (Button) view.findViewById(R.id.add);
            this.remove = (Button) view.findViewById(R.id.remove);

            this.lectureLayout.setOnClickListener(this);
            this.add.setOnClickListener(this);
            this.remove.setOnClickListener(this);
        }

        public interface ClickListener {
            /**
             * Called when the view is clicked.
             *
             * @param v view that is clicked
             * @param position of the clicked item
             */

            public void onClick(View v, int position);
        }

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v,getPosition());
            }
        }
    }

}