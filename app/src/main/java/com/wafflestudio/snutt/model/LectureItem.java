package com.wafflestudio.snutt.model;

import com.wafflestudio.snutt.SNUTTUtils;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureItem {

    public enum Type {
        Header(0),
        ItemTitle(1),
        ItemDetail(2),
        ItemButton(3),
        ItemColor(4),
        ItemClass(5);
        private final int value;
        Type(int value) {
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
    private Color color;
    private ClassTime classTime;
    private Type type;
    private boolean editable;

    public LectureItem(String title1, String value1, String title2, String value2, Type type, boolean editable) {
        this.title1 = title1;
        this.value1 = value1;
        this.title2 = title2;
        this.value2 = value2;
        this.type = type;
        this.editable = false;
    }

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

    public LectureItem(String title1, Color color, Type type) {
        this.title1 = title1;
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

    public LectureItem(Type type) {
        this.type = type;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
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
