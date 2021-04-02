package com.wafflestudio.snutt2.model;

import com.google.common.base.Strings;

/**
 * Created by makesource on 2017. 1. 23..
 */

public class User {
    private boolean isAdmin;
    private String email;
    private String local_id;
    private String fb_name;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String getEmail() {
        if (Strings.isNullOrEmpty(email)) return "(없음)";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFb_name() {
        return fb_name;
    }

    public void setFb_name(String fb_name) {
        this.fb_name = fb_name;
    }
}
