package com.wafflestudio.snutt2.model

/**
 * Created by makesource on 2016. 11. 20..
 */
class SettingsItem {
    enum class Type(val value: Int) {
        Account(0), Timetable(1), Developer(2), BugReport(3), License(4), Terms(5), Logout(6), Version(
            7
        ),
        Header(8), Id(9), ChangePassword(10), LinkFacebook(11), Email(12), ChangeEmail(13), Leave(
            14
        ),
        AddIdPassword(15), FacebookName(16), DeleteFacebook(17), Private(18);
    }

    enum class ViewType(val value: Int) {
        Header(0), ItemTitle(1);
    }

    var title: String? = null
    var detail: String? = null

    /*public void setViewType(ViewType viewType) {
         this.viewType = viewType;
     }*/ var type: Type

    constructor(type: Type) {
        this.type = type
    }

    constructor(title: String?, detail: String?, type: Type) {
        this.title = title
        this.detail = detail
        this.type = type
    }

    constructor(title: String?, type: Type) {
        this.title = title
        detail = ""
        this.type = type
    }

    val viewType: ViewType
        get() = if (type == Type.Header) ViewType.Header else ViewType.ItemTitle
}
