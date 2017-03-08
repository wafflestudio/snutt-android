package com.wafflestudio.snutt_staging.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.Lecture;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2017. 3. 7..
 */

public class LectureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "LECTURE_LIST_ADAPTER" ;
    private static ClickListener clickListener;

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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Lecture lecture = lectures.get(position);
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.bindData(lecture);
        if (selectedPosition == position) {
            holder.layout.setBackgroundColor(Color.DKGRAY);
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
            holder.layout.setBackgroundColor(Color.TRANSPARENT);
        }

        setOnItemClickListener(new ClickListener() {
            @Override
            public void onClick(View v, final int position) {
                //TODO : (Seongowon) 배경색 바꾸기 등등 시각적 효과 넣기
                //TODO : (Seongowon) 내 강의 리스트와 비교해서 이미 있는 강의면 remove를 없으면 add버튼을 활성화
                if (v.getId() == holder.layout.getId()) {
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
                    LectureManager.getInstance().addLecture(lecture, new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            notifyItemChanged(position);
                        }
                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                } else {
                    Log.d(TAG, String.valueOf(position) + " remove Clicked!!");
                    LectureManager.getInstance().removeLecture(lecture, new Callback() {
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
    public int getItemCount() {
        return lectures.size();
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
        notifyDataSetChanged();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected View layout;
        protected TextView title;
        protected TextView tag;
        protected TextView classTime;
        protected TextView location;
        protected Button add;
        protected Button remove;

        private ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            tag = (TextView) itemView.findViewById(R.id.tag);
            classTime = (TextView) itemView.findViewById(R.id.time);
            location = (TextView) itemView.findViewById(R.id.location);
            add = (Button) itemView.findViewById(R.id.add);
            remove = (Button) itemView.findViewById(R.id.remove);
            this.layout.setOnClickListener(this);
            this.add.setOnClickListener(this);
            this.remove.setOnClickListener(this);
        }

        private void bindData(Lecture lecture) {
            String titleText = "" ;
            titleText += lecture.getCourse_title();
            titleText += " (" + lecture.getInstructor() + " / " + String.valueOf(lecture.getCredit()) + "학점)";
            title.setText(titleText);

            String tagText = lecture.getCategory() + ", " + lecture.getDepartment() + ", "
                    + lecture.getAcademic_year();
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
                clickListener.onClick(v, getPosition());
            }
        }
    }

    private interface ClickListener {
        public void onClick(View v, int position);
    }

    private void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
