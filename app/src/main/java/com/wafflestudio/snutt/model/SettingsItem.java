package com.wafflestudio.snutt.model;

/**
 * Created by makesource on 2016. 11. 20..
 */

public class SettingsItem {

    public enum Type {
        Account(0),
        Timetable(1),
        Developer(2),
        BugReport(3),
        License(4),
        Terms(5),
        Logout(6),
        Version(7),
        Header(8),
        Id(9),
        ChangePassword(10),
        LinkFacebook(11),
        Email(12),
        ChangeEmail(13),
        Leave(14),
        AddIdPassword(15),
        FacebookName(16),
        DeleteFacebook(17),
        Private(18);
        private final int value;
        Type(int value) {
            this.value = value;
        }
        public final int getValue() {
            return value;
        }
    }

    public enum ViewType {
        Header(0),
        ItemTitle(1);
        private final int value;
        ViewType(int value) {
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

    public ViewType getViewType() {
        if (type == Type.Header) return ViewType.Header;
        return ViewType.ItemTitle;
    }

    /*public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }*/

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
