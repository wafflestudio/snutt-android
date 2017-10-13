package com.wafflestudio.snutt.model;

import java.util.List;

/**
 * Created by makesource on 2017. 5. 21..
 */

public class ColorList {
    private String message;
    private List<Color> colors;
    private List<String> names;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
