package com.wafflestudio.snutt.manager;

import com.wafflestudio.snutt.model.Lecture;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 2. 7..
 */
public class LectureManager {

    private ArrayList<Lecture> lectures;
    private ArrayList<Lecture> myLectures;

    private static LectureManager singleton;

    /**
     * LectureManager 싱글톤
     */

    public static LectureManager getInstance() {
        if(singleton == null) {
            singleton = new LectureManager();
        }
        return singleton;
    }
}
