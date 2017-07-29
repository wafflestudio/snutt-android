package com.wafflestudio.snutt_staging.model;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureItem {

    public enum Type {
        Title(0),
        Instructor(1),
        Color(2),
        Department(3),
        AcademicYear(4),
        Credit(5),
        Classification(6),
        Category(7),
        CourseNumber(8),
        LectureNumber(9),
        Remark(10),
        ClassTime(11),
        Syllabus(12),
        RemoveLecture(13),
        AddClassTime(14),
        ResetLecture(15),
        ShortHeader(16),
        LongHeader(17),
        ClassTimeHeader(18);
        private final int value;
        Type(final int value) {
            this.value = value;
        }
        public final int getValue() {
            return value;
        }
    }

    public enum ViewType {
        ItemShortHeader(0),
        ItemLongHeader(1),
        ItemTitle(2),
        ItemButton(3),
        ItemColor(4),
        ItemClass(5),
        ItemRemark(6),
        ItemClassTimeHeader(7);
        private final int value;
        ViewType(final int value) {
            this.value = value;
        }
        public final int getValue() {
            return value;
        }
    }

    private String title1;
    private String value1;
    private String title2;
    private String value2;
    private int colorIndex;
    private Color color;
    private ClassTime classTime;
    private Type type;
    private boolean editable;

    public LectureItem(String title1, String value1, String title2, String value2, Type type) {
        this.title1 = title1;
        this.value1 = value1;
        this.title2 = title2;
        this.value2 = value2;
        this.type = type;
        this.editable = false;
    }

    public LectureItem(String title1, String value1, Type type) {
        this.title1 = title1;
        this.value1 = value1;
        this.type = type;
        this.editable = false;
    }

    public LectureItem(String title1, int index, Color color, Type type) {
        this.title1 = title1;
        this.colorIndex = index;
        this.color = color;
        this.type = type;
        this.editable = false;
    }

    public LectureItem(ClassTime classTime, Type type, boolean editable) {
        this.classTime = classTime;
        this.type = type;
        this.editable = editable;
    }

    public LectureItem(ClassTime classTime, Type type) {
        this.classTime = classTime;
        this.type = type;
        this.editable = false;
    }

    public LectureItem(Type type, boolean editable) {
        this.type = type;
        this.editable = editable;
    }

    public LectureItem(Type type) {
        this.type = type;
    }

    public ViewType getViewType() {
        switch (type) {
            case ShortHeader:
                return ViewType.ItemShortHeader;
            case LongHeader:
                return ViewType.ItemLongHeader;
            case ClassTimeHeader:
                return ViewType.ItemClassTimeHeader;
            case Title:
            case Instructor:
            case Department:
            case AcademicYear:
            case Credit:
            case Classification:
            case Category:
            case CourseNumber:
            case LectureNumber:
                return ViewType.ItemTitle;
            case Color:
                return ViewType.ItemColor;
            case ClassTime:
                return ViewType.ItemClass;
            case Remark:
                return ViewType.ItemRemark;
            default:
                return ViewType.ItemButton;
        }
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int index) {
        this.colorIndex = index;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.colorIndex = 0;
        this.color = color;
    }

    public ClassTime getClassTime() {
        return classTime;
    }

    public void setClassTime(ClassTime classTime) {
        this.classTime = classTime;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
