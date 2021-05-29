package com.wafflestudio.snutt2.manager

import android.util.Log
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Tag
import com.wafflestudio.snutt2.model.TagType
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

/**
 * Created by makesource on 2016. 2. 23..
 */
class TagManager private constructor(app: SNUTTApplication) {
    // for search
    private val tags: MutableList<Tag>
    private val tagsMap: MutableMap<String, Tag>
    var searchEmptyClass: Boolean

    // for api post
    private var classification: MutableList<String>
    private var credit: MutableList<String>
    private var academic_year: MutableList<String>
    private var instructor: MutableList<String>
    private var department: MutableList<String>
    private var category: MutableList<String>
    private var time: MutableList<String>
    private val myTags: MutableList<Tag>
    private val app: SNUTTApplication

    interface OnTagChangedListener {
        fun notifyMyTagChanged(anim: Boolean)
        fun notifyTagListChanged()
    }

    private var listener: OnTagChangedListener? = null
    fun registerListener(fragment: OnTagChangedListener?) {
        listener = fragment
    }

    fun unregisterListener() {
        listener = null
    }

    fun reset() {
        tags.clear()
        classification.clear()
        credit.clear()
        academic_year.clear()
        instructor.clear()
        department.clear()
        category.clear()
        time.clear()
        tagsMap.clear()
        myTags.clear()
        searchEmptyClass = false
    }

    fun addTag(query: String): Boolean {
        if (!tagsMap.containsKey(query.toLowerCase())) return false
        addTag(tagsMap[query])
        return true
    }

    fun addTag(tag: Tag?): Boolean {
        val type = tag!!.tagType
        when (type) {
            TagType.CLASSIFICATION -> classification.add(tag.name)
            TagType.CREDIT -> credit.add(tag.name)
            TagType.ACADEMIC_YEAR -> academic_year.add(tag.name)
            TagType.INSTRUCTOR -> instructor.add(tag.name)
            TagType.DEPARTMENT -> department.add(tag.name)
            TagType.CATEGORY -> category.add(tag.name)
        }
        Log.d(TAG, "a tag is successfully added!!!")
        myTags.add(0, tag)
        notifyMyTagChanged(true)
        return true
    }

    fun removeTag(position: Int) {
        val tag = myTags[position]
        when (tag!!.tagType) {
            TagType.CLASSIFICATION -> classification.remove(tag.name)
            TagType.CREDIT -> credit.remove(tag.name)
            TagType.ACADEMIC_YEAR -> academic_year.remove(tag.name)
            TagType.INSTRUCTOR -> instructor.remove(tag.name)
            TagType.DEPARTMENT -> department.remove(tag.name)
            TagType.CATEGORY -> category.remove(tag.name)
        }
        myTags.removeAt(position)
        notifyMyTagChanged(true)
    }

    fun getTags(): List<Tag> {
        return tags
    }

    fun getMyTags(): List<Tag> {
        return myTags
    }

    fun updateNewTag(year: Int, semester: Int) {
        app.restService!!.getTagList(year, semester)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    Log.d(TAG, "update new tags Success!!")
                    reset()
                    for (name in it.classification) {
                        val tag = Tag(name, TagType.CLASSIFICATION)
                        tagsMap[name.toLowerCase()] = tag
                        tags.add(tag)
                    }
                    for (name in it.credit) {
                        val tag = Tag(name, TagType.CREDIT)
                        tagsMap[name.toLowerCase()] = tag
                        tags.add(tag)
                    }
                    for (name in it.academicYear) {
                        val tag = Tag(name, TagType.ACADEMIC_YEAR)
                        tagsMap[name.toLowerCase()] = tag
                        tags.add(tag)
                    }
                    for (name in it.instructor) {
                        val tag = Tag(name, TagType.INSTRUCTOR)
                        tagsMap[name.toLowerCase()] = tag
                        tags.add(tag)
                    }
                    for (name in it.department) {
                        val tag = Tag(name, TagType.DEPARTMENT)
                        tagsMap[name.toLowerCase()] = tag
                        tags.add(tag)
                    }
                    for (name in it.category) {
                        val tag = Tag(name.toLowerCase(), TagType.CATEGORY)
                        tagsMap[name] = tag
                        tags.add(tag)
                    }
                    notifyMyTagChanged(false)
                    notifyTagListChanged()
                },
                onError = {

                    Log.d(TAG, "update new tags failed...")
                }
            )
    }

    /* Below method will used when post query */
    fun getClassification(): List<String> {
        return classification
    }

    fun setClassification(classification: MutableList<String>) {
        this.classification = classification
    }

    fun getCredit(): List<String> {
        val integerCredit: MutableList<String> = ArrayList()
        for (c in credit) {
            integerCredit.add(c.substring(0, c.length - 2))
        }
        return integerCredit
    }

    fun setCredit(credit: MutableList<String>) {
        this.credit = credit
    }

    fun getAcademic_year(): List<String> {
        return academic_year
    }

    fun setAcademic_year(academic_year: MutableList<String>) {
        this.academic_year = academic_year
    }

    fun getInstructor(): List<String> {
        return instructor
    }

    fun setInstructor(instructor: MutableList<String>) {
        this.instructor = instructor
    }

    fun getDepartment(): List<String> {
        return department
    }

    fun setDepartment(department: MutableList<String>) {
        this.department = department
    }

    fun getCategory(): List<String> {
        return category
    }

    fun setCategory(category: MutableList<String>) {
        this.category = category
    }

    fun getTime(): List<String> {
        return time
    }

    fun setTime(time: MutableList<String>) {
        this.time = time
    }

    fun toggleSearchEmptyClass(): Boolean {
        searchEmptyClass = !searchEmptyClass
        return searchEmptyClass
    }

    private fun notifyMyTagChanged(anim: Boolean) {
        if (listener == null) return
        listener!!.notifyMyTagChanged(anim)
    }

    private fun notifyTagListChanged() {
        if (listener == null) return
        listener!!.notifyTagListChanged()
    }

    companion object {
        private const val TAG = "TAG_MANAGER"
        private var singleton: TagManager? = null
        fun getInstance(app: SNUTTApplication): TagManager? {
            if (singleton == null) {
                singleton = TagManager(app)
            }
            return singleton
        }

        @JvmStatic
        val instance: TagManager?
            get() {
                if (singleton == null) Log.e(TAG, "This method should not be called at this time!!")
                return singleton
            }
    }

    /**
     * TableManager 싱글톤
     */
    init {
        tags = ArrayList()
        classification = ArrayList()
        credit = ArrayList()
        academic_year = ArrayList()
        instructor = ArrayList()
        department = ArrayList()
        category = ArrayList()
        time = ArrayList()
        tagsMap = HashMap()
        myTags = ArrayList()
        searchEmptyClass = false
        this.app = app
    }
}
