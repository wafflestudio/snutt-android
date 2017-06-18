package com.wafflestudio.snutt_staging.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.manager.PrefManager;
import com.wafflestudio.snutt_staging.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 1. 24..
 */
public class TableView extends View {

    private static String TAG = "VIEW_TAG_TABLE_VIEW";

    private Paint backgroundPaint;
    private Paint linePaint, linePaint2, topLabelTextPaint, leftLabelTextPaint, leftLabelTextPaint2;
    private Paint lectureTextPaint;
    private Path path;
    private Context mContext;
    private String[] wdays;
    private float leftLabelWidth = SNUTTApplication.dpTopx(20);
    private float topLabelHeight = SNUTTApplication.dpTopx(25);
    private float unitWidth, unitHeight;
    private TextRect lectureTextRect;

    private List<Lecture> lectures ;
    private boolean export; // 현재 선택한 강의를 보여줄지 말지?

    // 시간표 trim 용
    private int numWidth;
    private int startWidth;
    private int numHeight;
    private int startHeight;

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        export = context.obtainStyledAttributes(attrs, R.styleable.TimeTableView).getBoolean(
                R.styleable.TimeTableView_export, false);

        lectures = LectureManager.getInstance().getLectures();
        mContext = context;
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        unitHeight = (getHeight() - topLabelHeight) / (float) (numHeight * 2);
        unitWidth = (getWidth() - leftLabelWidth) / (float) numWidth;
        invalidate();
    }

    void init(){
        setDrawingCacheEnabled(true);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xfff3f3f3);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xffcccccc);
        linePaint.setStrokeWidth(1);

        linePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint2.setStyle(Paint.Style.STROKE);
        linePaint2.setColor(0xffcccccc);
        linePaint2.setStrokeWidth(1);
        linePaint2.setPathEffect(new DashPathEffect(new float[]{5,5}, 2));

        path = new Path();

        topLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        topLabelTextPaint.setColor(0xff404040);
        topLabelTextPaint.setTextSize(SNUTTApplication.spTopx(11.5f));
        topLabelTextPaint.setTextAlign(Paint.Align.CENTER);

        leftLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        leftLabelTextPaint.setFakeBoldText(true);
        leftLabelTextPaint.setColor(0xff999999);
        leftLabelTextPaint.setTextSize(SNUTTApplication.spTopx(14));
        leftLabelTextPaint.setTextAlign(Paint.Align.CENTER);
        leftLabelTextPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        leftLabelTextPaint2.setColor(0xff999999);
        leftLabelTextPaint2.setTextSize(SNUTTApplication.spTopx(10));
        leftLabelTextPaint2.setTextAlign(Paint.Align.CENTER);

        wdays = new String[7];
        wdays[0] = mContext.getResources().getString(R.string.wday_mon);
        wdays[1] = mContext.getResources().getString(R.string.wday_tue);
        wdays[2] = mContext.getResources().getString(R.string.wday_wed);
        wdays[3] = mContext.getResources().getString(R.string.wday_thu);
        wdays[4] = mContext.getResources().getString(R.string.wday_fri);
        wdays[5] = mContext.getResources().getString(R.string.wday_sat);
        wdays[6] = mContext.getResources().getString(R.string.wday_sun);

        lectureTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //lectureTextPaint.setColor(0xff000000);
        lectureTextPaint.setTextSize(SNUTTApplication.spTopx(11));

        lectureTextRect = new TextRect(lectureTextPaint);

        numWidth = 7;
        startWidth = 0;
        numHeight = 14;
        startHeight = 0;
    }

    float getTextWidth(String text, Paint paint){
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }
    float getTextHeight(String text, Paint paint){
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        int wday = (int) ((x - leftLabelWidth) / unitWidth) + startWidth;
        float time = ((int) ((y - topLabelHeight) / unitHeight)) / 2f + (float) startHeight;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "action down");
            Log.d(TAG, "day : " + String.valueOf(wday));
            Log.d(TAG, "time : " + String.valueOf(time));


            //터치한 게 내 강의 중 하나


        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "action up");
            Log.d(TAG, "day : " + String.valueOf(wday));
            Log.d(TAG, "time : " + String.valueOf(time));
            //터치한 게 내 강의 중 하나
            for (int i = 0; i < lectures.size(); i++) {
                Lecture lecture = lectures.get(i);
                if (LectureManager.getInstance().contains(lecture, wday, time)) {
                    getActivity().startLectureMain(i);
                }
            }
        }  else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //x, y를 교시로

            /*if (LectureManager.getInstance().existCustomLecture()) {
                float duration = time - LectureManager.getInstance().getCustomStartTime() + 0.5f;
                if (!LectureManager.getInstance().alreadyExistClassTime(duration) && custom) {
                    Log.d(TAG, "update custom lecture duration");
                    LectureManager.getInstance().setCustomDuration(duration);
                }
            }*/
        }

        /*    boolean lectureClicked = false;
            //터치한 게 selectedLecture이면
            if (Lecture.selectedLecture != null && Lecture.selectedLecture.contains(wday, time)){
                lectureClicked = true;
            }
            //터치한 게 내 강의 중 하나
            for (int i=0;i<Lecture.myLectures.size();i++){
                if (Lecture.myLectures.get(i).contains(wday, time)){
                    lectureClicked = true;
                }
            }
            //빈 공간 클릭
            if (lectureClicked == false && mContext.getCustomEditable()){
                mCustomStartTime = time;
                mCustomWday = wday;
                mCustomDuration = 0.5f;
            } else {
                resetCustomVariables();
            }
            invalidate();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE){
            //x, y를 교시로
            float time = ((int) ((y - topLabelHeight) / unitHeight)) / 2f;

            if (mCustomStartTime != -1 && mCustomWday != -1){
                float duration = time - mCustomStartTime + 0.5f;
                Lecture tmpLecture = new Lecture("", "", mCustomWday, mCustomStartTime, duration);
                if (!tmpLecture.alreadyExistClassTime() && mContext.getCustomEditable()){
                    mCustomDuration = duration;
                }
            }

            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            //x, y를 요일, 교시로
            int wday = (int) ((x - leftLabelWidth) / unitWidth);
            float time = ((int) ((y - topLabelHeight) / unitHeight)) / 2f;

            boolean lectureClicked = false;
            //터치한 게 selectedLecture이면 강의 추가
            if (Lecture.selectedLecture != null && Lecture.selectedLecture.contains(wday, time)){
                Lecture.addMyLecture(mContext, Lecture.selectedLecture);
                lectureClicked = true;
            }
            //터치한 게 내 강의 중 하나
            for (int i=0;i<Lecture.myLectures.size();i++){
                if (Lecture.myLectures.get(i).contains(wday, time)){
                    lectureClicked = true;
                    Lecture.myLectures.get(i).setNextColor();
                    invalidate();
                    Lecture.saveMyLectures();
                }
            }
            //빈 공간 클릭 시선택 해제
            if (lectureClicked == false){
                Lecture.selectedLecture = null;
                if (TimetableView.mInstance != null)
                    TimetableView.mInstance.invalidate();
                if (MainActivity.mSearchAdapter != null)
                    MainActivity.mSearchAdapter.notifyDataSetChanged();
            }

            if (mCustomWday != -1 && mCustomStartTime != -1 && mCustomDuration > 0 ){
                if (mContext.getCustomEditable()){
                    new CustomLectureDialog(mContext, mCustomWday, mCustomStartTime, mCustomDuration).show();
                }
            }

            resetCustomVariables();
            invalidate();*/

        return true;
    }

    //주어진 canvas에 시간표를 그림
    public void drawTimetable(Canvas canvas, int canvasWidth, int canvasHeight, boolean export){

        if (export) {
            if (PrefManager.getInstance().getAutoTrim()) {
                int startWday = 7;
                int endWday = 0;
                int startTime = 14;
                int endTime = 0;
                for (Lecture lecture : lectures) {
                    for (JsonElement element : lecture.getClass_time_json()) {
                        JsonObject classTime = element.getAsJsonObject();
                        int wday = classTime.get("day").getAsInt();
                        float start = classTime.get("start").getAsFloat();
                        float duration = classTime.get("len").getAsFloat();
                        startWday = Math.min(startWday, wday);
                        endWday = Math.max(endWday, wday);
                        startTime = Math.min(startTime, (int) start); // 버림
                        endTime = Math.max(endTime, (int)(start + duration + 0.5f)); // 반올림
                    }
                }
                // 월 : 0 , 화 : 1 , ... 금 : 4, 토 : 5
                startWidth = 0;
                numWidth = Math.max(5, endWday + 1);
                //
                startHeight = Math.min(1, startTime);
                numHeight = Math.max(10, endTime - startHeight);
            } else {
                startWidth = PrefManager.getInstance().getTrimWidthStart();
                numWidth = PrefManager.getInstance().getTrimWidthNum();
                startHeight = PrefManager.getInstance().getTrimHeightStart();
                numHeight = PrefManager.getInstance().getTrimHeightNum();
            }
        }

        unitHeight = (canvasHeight - topLabelHeight) / (float) (numHeight * 2);
        unitWidth = (canvasWidth - leftLabelWidth) / (float) numWidth;

        //가로 줄 28개
        canvas.drawLine(0, 0, canvasWidth, 0, linePaint);
        canvas.drawLine(0, canvasHeight, canvasWidth, canvasHeight, linePaint);
        for (int i=0;i<numHeight*2;i++){
            float height = topLabelHeight + unitHeight * i;
            if (i%2 == 1) {
                path.moveTo(leftLabelWidth, height);
                path.lineTo(canvasWidth, height);
                //canvas.drawPath(path, linePaint2);
                canvas.drawLine(leftLabelWidth, height,canvasWidth, height,linePaint);
            }
            else {
                canvas.drawLine(leftLabelWidth / 3f, height, canvasWidth, height, linePaint);
            }
        }
        //세로 줄 그리기
        for (int i=0;i<numWidth;i++){
            float width = leftLabelWidth + unitWidth * i;
            float textHeight = getTextHeight(wdays[0], topLabelTextPaint);
            canvas.drawLine(width, 0, width, canvasHeight, linePaint);
            canvas.drawText(wdays[i + startWidth], (leftLabelWidth + unitWidth * (i+0.5f)), (topLabelHeight+textHeight)/2f, topLabelTextPaint);
        }
        canvas.drawLine(0, 0, 0, canvasHeight, linePaint);
        canvas.drawLine(canvasWidth, 0, canvasWidth, canvasHeight, linePaint);
        //교시 텍스트 그리기
        for (int i=0;i<numHeight;i++){
            String str1 = i + startHeight + "교시";
            String str2 = SNUTTUtils.zeroStr(i+startHeight+8) + ":00~" + SNUTTUtils.zeroStr(i+startHeight+9) + ":00";
            String str = String.valueOf(i + startHeight + 8);
            float textHeight = getTextHeight(str1, leftLabelTextPaint);
            float textHeight2 = getTextHeight(str2, leftLabelTextPaint2);
            float padding = SNUTTApplication.dpTopx(5);
            if (canvasWidth > canvasHeight) padding = 0;
            float height = topLabelHeight + unitHeight * (i * 2 + 1) + (textHeight + textHeight2 + padding) / 2f;
            //canvas.drawText(str1, leftLabelWidth/2f, height - textHeight2 - padding, leftLabelTextPaint);
            //canvas.drawText(str2, leftLabelWidth/2f, height, leftLabelTextPaint2);

            canvas.drawText(str,  leftLabelWidth/2f, topLabelHeight + unitHeight * (i * 2) + unitHeight/2f, leftLabelTextPaint2);
        }
        //내 강의 그리기
        if (lectures != null) {
            for (int i = 0; i < lectures.size(); i++) {
                Lecture lecture = lectures.get(i);
                if (lecture.getColorIndex() == 0) drawLecture(canvas, canvasWidth, canvasHeight, lecture, lecture.getBgColor(), lecture.getFgColor());
                else drawLecture(canvas, canvasWidth, canvasHeight, lecture, lecture.getColorIndex());
            }
        }

        if (!export) {
            //현재 선택한 강의 그리기
            Lecture selectedLecture = LectureManager.getInstance().getSelectedLecture();
            if (selectedLecture != null && !LectureManager.getInstance().alreadyOwned(selectedLecture)){
                drawLecture(canvas, canvasWidth, canvasHeight,selectedLecture, LectureManager.getInstance().getDefaultBgColor(), LectureManager.getInstance().getDefaultFgColor());
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.reset(); // to remove line which drawn by drawPath
        drawTimetable(canvas, getWidth(), getHeight(), export);
    }

    void drawLecture(Canvas canvas, float canvasWidth, float canvasHeight, Lecture lecture, int bgColor, int fgColor){
        //class_time : 수(6-2) -> {"day":2,"start":6,"len":2,"place":"301-118","_id":"569f967697f670df460ed3d8"}
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject classTime = element.getAsJsonObject();

            int wday = classTime.get("day").getAsInt();
            float startTime = classTime.get("start").getAsFloat();
            float duration = classTime.get("len").getAsFloat();
            String location = classTime.get("place").getAsString();
            drawClass(canvas, canvasWidth, canvasHeight, lecture.getCourse_title(), location, wday, startTime, duration, bgColor, fgColor);
        }
    }

    void drawLecture(Canvas canvas, float canvasWidth, float canvasHeight, Lecture lecture, int colorIndex){
        //class_time : 수(6-2) -> {"day":2,"start":6,"len":2,"place":"301-118","_id":"569f967697f670df460ed3d8"}
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject classTime = element.getAsJsonObject();

            int wday = classTime.get("day").getAsInt();
            float startTime = classTime.get("start").getAsFloat();
            float duration = classTime.get("len").getAsFloat();
            String location = classTime.get("place").getAsString();
            int bgColor = LectureManager.getInstance().getBgColorByIndex(colorIndex);
            int fgColor = LectureManager.getInstance().getFgColorByIndex(colorIndex);
            drawClass(canvas, canvasWidth, canvasHeight, lecture.getCourse_title(), location, wday, startTime, duration, bgColor, fgColor);
        }
    }


    //사각형 하나를 그림
    void drawClass(Canvas canvas, float canvasWidth, float canvasHeight, String course_title, String location, int wday, float startTime, float duration, int bgColor, int fgColor){
        float unitHeight = (canvasHeight - topLabelHeight) / (float) (numHeight * 2);
        float unitWidth = (canvasWidth - leftLabelWidth) / (float) numWidth;
        if (wday - startWidth < 0) return; // 날자가 잘리는 경우
        if ((startTime - startHeight) * unitHeight * 2 + (unitHeight * duration * 2) < 0) return; // 교시가 잘리는 경우

        //startTime : 시작 교시
        float left = leftLabelWidth + (wday - startWidth) * unitWidth;
        float right = leftLabelWidth + (wday - startWidth) * unitWidth + unitWidth;
        float top = topLabelHeight + Math.max(0, (startTime - startHeight)) * unitHeight * 2;
        float bottom = topLabelHeight + (startTime - startHeight) * unitHeight * 2 + (unitHeight * duration * 2);
        float borderWidth = SNUTTApplication.dpTopx(3);
        RectF r = new RectF(left, top, right, bottom);
        Paint p = new Paint();
        p.setColor(bgColor);
        canvas.drawRect(r, p);
        //canvas.drawRoundRect(r, 20, 20, p);
        //canvas.drawRect(left, top, right, bottom, lectureBorderPaint[colorIndex]);
        //canvas.drawRect(left+borderWidth, top+borderWidth, right-borderWidth, bottom-borderWidth, paints[colorIndex]);
        //강의명, 강의실 기록
        String str = course_title + "\n" + location;
        int width = (int)(right - left);
        int height = (int)(bottom - top);
        int strHeight = lectureTextRect.prepare(str, width, height);
        lectureTextRect.draw(canvas, (int)left, (int)(top + (height - strHeight)/2), width, fgColor);
    }

    //사용자 정의 시간표용..
    /*void drawCustomBox(Canvas canvas, float canvasWidth, float canvasHeight, int wday, float startTime, float duration){
        if (!custom) return;

        float unitHeight = (canvasHeight - topLabelHeight) / (float) (numHeight * 2);
        float unitWidth = (canvasWidth - leftLabelWidth) / (float) numWidth;

        //startTime : 시작 교시
        float left = leftLabelWidth + wday * unitWidth;
        float right = leftLabelWidth + wday * unitWidth + unitWidth;
        float top = topLabelHeight + startTime * unitHeight * 2;
        float bottom = topLabelHeight + startTime * unitHeight * 2 + (unitHeight * duration * 2);
        float borderWidth = SNUTTApplication.dpTopx(3);

        mCustomPaint.setColor(Color.RED);
        mCustomPaint.setStyle(Paint.Style.STROKE);  // 테두리만
        mCustomPaint.setStrokeWidth(borderWidth);
        mCustomPaint.setPathEffect(new DashPathEffect(new float[] {SNUTTApplication.dpTopx(6),SNUTTApplication.dpTopx(3)}, 0));

//		canvas.drawRect(left, top, right, bottom, mCustomPaint);
        canvas.drawRect(left+borderWidth/2, top+borderWidth/2, right-borderWidth/2, bottom-borderWidth/2, mCustomPaint);
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        int desiredWidth = w;
        int desiredHeight = h - getTabBarHeight() - getStatusBarHeight() - getActionBarHeight();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    private int getStatusBarHeight(){

        int statusHeight = 0;
        int screenSizeType = (mContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK);

        if(screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

            if (resourceId > 0) {
                statusHeight = mContext.getResources().getDimensionPixelSize(resourceId);
            }
        }

        return statusHeight;
    }

    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
        return actionBarHeight;
    }

    private int getTabBarHeight() {
        int tabBarHeight = 0;
        tabBarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.tab_bar_height);
        return tabBarHeight;
    }

    private SNUTTBaseActivity getActivity() {
        return (SNUTTBaseActivity) mContext;
    }
}
