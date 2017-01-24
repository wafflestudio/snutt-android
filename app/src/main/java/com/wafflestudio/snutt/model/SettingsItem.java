package com.wafflestudio.snutt.model;

/**
 * Created by makesource on 2016. 11. 20..
 */

public class SettingsItem {
    public enum Type {
        Header(0),
        ItemTitle(1);
        private final int value;
        Type(int value) {
            this.value = value;
        }
        public final int getValue() {
            return value;
        }
    }

    private String title;
    private String detail;

    private Type type;

    public SettingsItem(Type type) {
        this.type = type;
    }

    public SettingsItem(String title, String detail, Type type) {
        this.title = title;
        this.detail = detail;
        this.type = type;
    }

    public SettingsItem(String title, Type type) {
        this.title = title;
        this.detail = "";
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
