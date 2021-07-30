package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.GetTagListResults
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage
) {
    private var _tags: List<TagDto>
        get() = storage.tags.get()
        set(value) {
            storage.tags.update(value)
        }

    val tags = storage.tags.asObservable()

    fun fetchTags(): Single<GetTagListResults> {
        return storage.lastViewedTable.asObservable()
            .filterEmpty()
            .firstOrError()
            .flatMap {
                api.getTagList(it.year.toInt(), it.semester.toInt())
                    .observeOn(Schedulers.io())
            }
            .doOnSuccess {
                val list = mutableListOf<TagDto>()
                list.apply {
                    addAll(it.department.map { TagDto(TagType.DEPARTMENT, it) })
                    addAll(it.classification.map { TagDto(TagType.CLASSIFICATION, it) })
                    addAll(it.academicYear.map { TagDto(TagType.ACADEMIC_YEAR, it) })
                    addAll(it.credit.map { TagDto(TagType.CREDIT, it) })
                    addAll(it.instructor.map { TagDto(TagType.INSTRUCTOR, it) })
                    addAll(it.category.map { TagDto(TagType.CATEGORY, it) })
                }
                _tags = list.toList()
            }
    }
}
