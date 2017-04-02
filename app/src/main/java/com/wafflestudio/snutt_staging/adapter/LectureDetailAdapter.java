package com.wafflestudio.snutt_staging.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.ClassTime;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.LectureItem;
import com.wafflestudio.snutt_staging.model.Table;
import com.wafflestudio.snutt_staging.ui.LectureDetailFragment;
import com.wafflestudio.snutt_staging.ui.LectureMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.wafflestudio.snutt_staging.model.LectureItem.ViewType.ItemButton;
import static com.wafflestudio.snutt_staging.model.LectureItem.ViewType.ItemHeader;

/**
 * Created by makesource on 2017. 3. 17..
 */

public class LectureDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "LECTURE_DETAIL_ADAPTER";
    private static TextChangedListener textChangedListener;
    private static LongClickListener longClickListener;

    private List<LectureItem> lists;
    private LectureMainActivity activity;
    private LectureDetailFragment fragment;
    private Lecture lecture;

    private int day;
    private int fromTime;
    private int toTime;

    public LectureDetailAdapter(LectureMainActivity activity, LectureDetailFragment fragment, Lecture lecture, ArrayList<LectureItem> lists) {
        this.activity = activity;
        this.fragment = fragment;
        this.lists = lists;
        this.lecture = lecture;
        this.setOnTextChangedListener(new TextChangedListener() {
            @Override
            public void onText1Changed(String text, int position) {
                getItem(position).setValue1(text);
            }

            @Override
            public void onText2Changed(String text, int position) {
                getItem(position).setValue2(text);
            }

            @Override
            public void onLocationChanged(String text, int position) {
                getItem(position).getClassTime().setPlace(text);
            }
        });
        this.setOnLongClickListener(new LongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                showDeleteDialog(position);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "On create view holder called.");
        if (viewType == LectureItem.ViewType.ItemHeader.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_header, parent, false);
            return new HeaderViewHolder(view);
        }
        if (viewType == LectureItem.ViewType.ItemTitle.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_item_title, parent, false);
            return new TitleViewHolder(view);
        }
        if (viewType == LectureItem.ViewType.ItemDetail.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_item_detail, parent, false);
            return new DetailViewHolder(view);
        }
        if (viewType == LectureItem.ViewType.ItemButton.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_item_button, parent, false);
            return new ButtonViewHolder(view);
        }
        if (viewType == LectureItem.ViewType.ItemColor.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_item_color, parent, false);
            return new ColorViewHolder(view);
        }
        if (viewType == LectureItem.ViewType.ItemClass.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_item_class, parent, false);
            return new ClassViewHolder(view);
        }
        if (viewType == LectureItem.ViewType.ItemRemark.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_lecture_item_remark, parent, false);
            return new RemarkViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "On bind view holder called.");
        int viewType = getItemViewType(position);
        final LectureItem item = getItem(position);
        if (viewType == LectureItem.ViewType.ItemHeader.getValue()) {
            // do nothing
        }
        if (viewType == LectureItem.ViewType.ItemTitle.getValue()) {
            TitleViewHolder viewHolder = (TitleViewHolder) holder;
            viewHolder.bindData(item);
        }
        if (viewType == LectureItem.ViewType.ItemDetail.getValue()) {
            DetailViewHolder viewHolder = (DetailViewHolder) holder;
            viewHolder.bindData(item);
        }
        if (viewType == LectureItem.ViewType.ItemButton.getValue()) {
            ButtonViewHolder viewHolder = (ButtonViewHolder) holder;
            viewHolder.bindData(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (item.getType()) {
                        case Syllabus:
                            startSyllabus();
                            break;
                        case RemoveLecture:
                            startRemoveAlertView();
                            break;
                        case AddClassTime:
                            addClassItem();
                            notifyItemInserted(getLastClassItemPosition());
                            break;
                        case ResetLecture:
                            startResetAlertView();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        if (viewType == LectureItem.ViewType.ItemColor.getValue()) {
            ColorViewHolder viewHolder = (ColorViewHolder) holder;
            viewHolder.bindData(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isEditable()) {
                        activity.setColorPickerFragment();
                    }
                }
            });
        }
        if (viewType == LectureItem.ViewType.ItemClass.getValue()) {
            ClassViewHolder viewHolder = (ClassViewHolder) holder;
            viewHolder.bindData(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isEditable()) {
                        showDialog(item);
                    }
                }
            });
        }
        if (viewType == LectureItem.ViewType.ItemRemark.getValue()) {
            RemarkViewHolder viewHolder = (RemarkViewHolder) holder;
            viewHolder.bindData(item);
        }
    }

    private void addClassItem() {
        int pos = getLastClassItemPosition() + 1;
        lists.add(pos, new LectureItem(new ClassTime(0,0,1,""), LectureItem.Type.ClassTime, true));
    }

    private int getLastClassItemPosition() {
        for (int i = 0;i < getItemCount();i ++) {
            if (isLastClassItem(i)) return i;
        }
        Log.e(TAG, "can't find class time item");
        return -1;
    }

    private boolean isLastClassItem(int position) {
        if (position == getItemCount() - 1) return false;
        return (getItem(position).getType() == LectureItem.Type.ClassTime) && (getItem(position + 1).getType() != LectureItem.Type.ClassTime);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return lists.get(position).getViewType().getValue();
    }

    public LectureItem getItem(int position) {
        return lists.get(position);
    }

    public void updateLecture(Lecture lecture, Callback<Table> callback) {
        // 강의명, 교수, 학과, 학년, 학점, 분류, 구분, 강의시간 전체를 다 업데이트
        Log.d(TAG, "update lecture called.");
        Lecture target = new Lecture();
        JsonArray ja = new JsonArray();
        for (LectureItem item : lists) {
            if (item.getViewType() == ItemHeader) continue;
            if (item.getViewType() == ItemButton) continue;
            LectureItem.Type type = item.getType();

            switch (type) {
                case Title: // 강의명
                    target.setCourse_title(item.getValue1());
                    break;
                case Instructor: // 교수
                    target.setInstructor(item.getValue1());
                    break;
                case Color: // 색상
                    target.setBgColor(item.getColor().getBg());
                    target.setFgColor(item.getColor().getFg());
                    break;
                case Department: // 학과
                    target.setDepartment(item.getValue1());
                    break;
                case AcademicYearCredit: // 학년, 학점
                    target.setAcademic_year(item.getValue1());
                    target.setCredit(Integer.parseInt(item.getValue2()));
                    break;
                case ClassificationCategory: // 분류, 구분
                    target.setClassification(item.getValue1());
                    target.setCategory(item.getValue2());
                    break;
                case CourseNumberLectureNumber: // 강좌번호, 분반번호
                    break;
                case ClassTime:
                    JsonElement je = new Gson().toJsonTree(item.getClassTime());
                    ja.add(je);
                    break;
                case Remark: // 비고
                    target.setRemark(item.getValue1());
                    break;
                default:
                    break;
            }
        }
        target.setClass_time_json(ja);
        LectureManager.getInstance().updateLecture(lecture, target, callback);
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private HeaderViewHolder(View view) {
            super(view);
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private EditText value;

        private TitleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text_title);
            value = (EditText) view.findViewById(R.id.text_value);
        }
        private void bindData(final LectureItem item) {
            Log.d(TAG, item.getTitle1() + " " + item.getValue1());
            title.setText(item.getTitle1());
            value.setText(item.getValue1());
            value.setClickable(item.isEditable());
            value.setFocusable(item.isEditable());
            value.setFocusableInTouchMode(item.isEditable());
            value.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    textChangedListener.onText1Changed(s.toString(), getPosition());
                }
            });
        }
    }

    private static class DetailViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout title1;
        private TextInputLayout title2;
        private EditText editText1;
        private EditText editText2;

        private DetailViewHolder(View view) {
            super(view);
            title1 = (TextInputLayout) view.findViewById(R.id.input_title1);
            editText1 = (EditText) view.findViewById(R.id.input_detail1);
            title2 = (TextInputLayout) view.findViewById(R.id.input_title2);
            editText2 = (EditText) view.findViewById(R.id.input_detail2);
        }
        private void bindData(final LectureItem item) {
            title1.setHint(item.getTitle1());
            editText1.setText(item.getValue1());
            editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    textChangedListener.onText1Changed(s.toString(), getPosition());
                    //item.setValue1(s.toString());
                }
            });
            title2.setHint(item.getTitle2());
            editText2.setText(item.getValue2());
            editText2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    textChangedListener.onText2Changed(s.toString(), getPosition());

                }
            });
            Log.d(TAG, "item editable is changed.." + item.isEditable());
            editText1.setClickable(item.isEditable());
            editText1.setFocusable(item.isEditable());
            editText1.setFocusableInTouchMode(item.isEditable());
            editText2.setClickable(item.isEditable());
            editText2.setFocusable(item.isEditable());
            editText2.setFocusableInTouchMode(item.isEditable());
            if (item.getType() == LectureItem.Type.AcademicYearCredit) { // 학점
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        }
    }

    private static class ButtonViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout;
        private TextView textView;
        private ButtonViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.layout);
            textView = (TextView) view.findViewById(R.id.text_button);
        }
        private void bindData(final LectureItem item, View.OnClickListener listener) {
            layout.setOnClickListener(listener);
            switch (item.getType()) {
                case Syllabus:
                    textView.setText("강의계획서");
                    textView.setTextColor(Color.parseColor("#000000"));
                    break;
                case RemoveLecture:
                    textView.setText("삭제");
                    textView.setTextColor(Color.parseColor("#FF0000"));
                    break;
                case AddClassTime:
                    textView.setText("시간 추가");
                    textView.setTextColor(Color.parseColor("#000000"));
                    break;
                case ResetLecture:
                    textView.setText("초기화");
                    textView.setTextColor(Color.parseColor("#FF0000"));
                    break;
                default:
                    break;
            }
        }
    }

    private static class ColorViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout;
        private TextView title;
        private View fgColor;
        private View bgColor;

        private ColorViewHolder(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.layout);
            title = (TextView) view.findViewById(R.id.text_title);
            fgColor = (View) view.findViewById(R.id.fgColor);
            bgColor = (View) view.findViewById(R.id.bgColor);
        }
        private void bindData(final LectureItem item, View.OnClickListener listener) {
            title.setText("색상");
            layout.setOnClickListener(listener);
            bgColor.setBackgroundColor(item.getColor().getBg());
            fgColor.setBackgroundColor(item.getColor().getFg());
        }
    }

    private static class RemarkViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout title1;
        private EditText editText1;

        private RemarkViewHolder(View view) {
            super(view);
            title1 = (TextInputLayout) view.findViewById(R.id.input_title1);
            editText1 = (EditText) view.findViewById(R.id.input_detail1);
        }
        private void bindData(final LectureItem item) {
            title1.setHint(item.getTitle1());
            editText1.setText(item.getValue1());
            editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    textChangedListener.onText1Changed(s.toString(), getPosition());
                }
            });
            editText1.setClickable(item.isEditable());
            editText1.setFocusable(item.isEditable());
            editText1.setFocusableInTouchMode(item.isEditable());
        }
    }

    private static class ClassViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        private TextInputLayout title1;
        private TextInputLayout title2;
        private EditText editText1;
        private EditText editText2;

        private ClassViewHolder(View view) {
            super(view);
            title1 = (TextInputLayout) view.findViewById(R.id.input_title1);
            title2 = (TextInputLayout) view.findViewById(R.id.input_title2);
            editText1 = (EditText) view.findViewById(R.id.input_time);
            editText2 = (EditText) view.findViewById(R.id.input_location);
            title1.setOnLongClickListener(this);
            editText1.setOnLongClickListener(this);
            title2.setOnLongClickListener(this);
            editText2.setOnLongClickListener(this);
            view.setOnLongClickListener(this);
        }
        private void bindData(final LectureItem item, View.OnClickListener listener) {
            title1.setHint("시간");
            String time = SNUTTUtils.numberToWday(item.getClassTime().getDay()) + " " +
                    SNUTTUtils.numberToTime(item.getClassTime().getStart()) + "~" +
                    SNUTTUtils.numberToTime(item.getClassTime().getStart() + item.getClassTime().getLen());
            editText1.setText(time);
            editText1.setClickable(false);
            editText1.setFocusable(false);
            editText1.setOnClickListener(listener);
            title2.setHint("장소");
            editText2.setText(item.getClassTime().getPlace());
            editText2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    textChangedListener.onLocationChanged(s.toString(), getPosition());
                    //item.getClassTime().setPlace(s.toString());
                }
            });
            editText2.setClickable(item.isEditable());
            editText2.setFocusable(item.isEditable());
            editText2.setFocusableInTouchMode(item.isEditable());
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                longClickListener.onLongClick(v, getPosition());
                return true;
            }
            return false;
        }
    }

    private void showDialog(final LectureItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // item's class time update
                ClassTime t = new ClassTime(day, fromTime / 2f, (toTime-fromTime) / 2f, item.getClassTime().getPlace());
                item.setClassTime(t);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.dialog_time_picker, null);
        alert.setView(layout);
        alert.show();

        NumberPicker dayPicker = (NumberPicker) layout.findViewById(R.id.dayPicker);
        NumberPicker fromPicker = (NumberPicker) layout.findViewById(R.id.timeFrom);
        final NumberPicker toPicker = (NumberPicker) layout.findViewById(R.id.timeTo);

        day = item.getClassTime().getDay();
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(6);
        dayPicker.setDisplayedValues(days);
        dayPicker.setValue(day);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                day = newVal;
            }
        });

        // used integer interval (origin value * 2) to use number picker
        fromTime = (int) (item.getClassTime().getStart() * 2);
        String[] from = SNUTTUtils.getTimeList(0, 27);
        fromPicker.setMinValue(0);
        fromPicker.setMaxValue(27);
        fromPicker.setDisplayedValues(from);
        fromPicker.setValue(fromTime);
        fromPicker.setWrapSelectorWheel(false);
        fromPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                fromTime = newVal;
                /* set DisplayedValues as null to avoid out of bound index error */
                toPicker.setDisplayedValues(null);
                toPicker.setValue(fromTime + 1);
                toPicker.setMinValue(fromTime + 1);
                toPicker.setMaxValue(28);
                toPicker.setDisplayedValues(SNUTTUtils.getTimeList(fromTime + 1, 28));
                /* setValue method does not call listener, so we have to change the value manually */
                toTime = fromTime + 1;
            }
        });

        toTime = (int) (item.getClassTime().getStart()+item.getClassTime().getLen())*2;
        String[] to = SNUTTUtils.getTimeList(fromTime+1, 28);
        toPicker.setMinValue(fromTime+1);
        toPicker.setMaxValue(28);
        toPicker.setDisplayedValues(to);
        toPicker.setValue(toTime);
        toPicker.setWrapSelectorWheel(false);
        toPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                toTime = newVal;
            }
        });
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lists.remove(position);
                notifyItemRemoved(position);
                dialog.dismiss();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setTitle("시간을 삭제하시겠습니까?");
        alert.show();
    }

    private void startSyllabus() {
        LectureManager.getInstance().getCoursebookUrl(lecture.getCourse_number(), lecture.getLecture_number(), new Callback<Map>() {
            public void success(Map map, Response response) {
                String url = (String) map.get("url");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void startRemoveAlertView() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("강좌 삭제");
        alert.setMessage("강좌를 삭제하시겠습니까");
        alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                LectureManager.getInstance().removeLecture(lecture, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        activity.finish();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void startResetAlertView() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("강좌 초기화");
        alert.setMessage("강좌를  원래 상태로 초기화하시겠습니까");
        alert.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                LectureManager.getInstance().resetLecture(lecture, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        Lecture updated = LectureManager.getInstance().getLectureById(lecture.getId());
                        lecture.setCourse_title(updated.getCourse_title());
                        lecture.setInstructor(updated.getInstructor());
                        lecture.setColor(updated.getColor());
                        lecture.setDepartment(updated.getDepartment());
                        lecture.setAcademic_year(updated.getAcademic_year());
                        lecture.setCredit(updated.getCredit());
                        lecture.setClassification(updated.getClassification());
                        lecture.setCategory(updated.getCategory());
                        lecture.setClass_time(updated.getClass_time());
                        lecture.setClass_time_json(updated.getClass_time_json());
                        lecture.setRemark(updated.getRemark());
                        fragment.refreshFragment();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private interface TextChangedListener {
        public void onText1Changed(String text, int position);
        public void onText2Changed(String text, int position);
        public void onLocationChanged(String text, int position);
    }

    private void setOnTextChangedListener(TextChangedListener textChangedListener) {
        this.textChangedListener = textChangedListener;
    }

    private interface LongClickListener {
        public void onLongClick(View view, int position);
    }

    private void setOnLongClickListener(LongClickListener listener) {
        this.longClickListener = listener;
    }

}
