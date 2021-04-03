package com.wafflestudio.snutt2.model

import com.google.gson.JsonObject

/**
 * Created by makesource on 2016. 2. 10..
 */
class ClassTime {
    var day: Int
    var start: Float
    var len: Float
    var place: String
    private var _id: String? = null

    constructor(day: Int, start: Float, len: Float, place: String) {
        this.day = day
        this.start = start
        this.len = len
        this.place = place
    }

    constructor(jsonObject: JsonObject) {
        day = jsonObject["day"].asInt
        start = jsonObject["start"].asFloat
        len = jsonObject["len"].asFloat
        place = jsonObject["place"].asString
        //this._id = jsonObject.get("_id").getAsString();
    }

    fun get_id(): String? {
        return _id
    }

    fun set_id(_id: String?) {
        this._id = _id
    }
}