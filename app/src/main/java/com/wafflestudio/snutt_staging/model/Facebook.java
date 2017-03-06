package com.wafflestudio.snutt_staging.model;

/**
 * Created by makesource on 2017. 2. 15..
 */

public class Facebook {
    private boolean attached;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAttached() {
        return attached;
    }

    public void setAttached(boolean attached) {
        this.attached = attached;
    }
}
