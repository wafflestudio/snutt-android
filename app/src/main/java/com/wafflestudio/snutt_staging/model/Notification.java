package com.wafflestudio.snutt_staging.model;

/**
 * Created by makesource on 2016. 11. 20..
 */

public class Notification {
    private String _id;
    private String message;
    private String created_at;
    private int type;

    public Notification(String _id, String message, int type) {
        this._id = _id;
        this.message = message;
        this.type = type;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }
}
