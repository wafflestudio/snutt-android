package com.wafflestudio.snutt2.di

import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.course_books.CourseBookRepositoryImpl
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepositoryImpl
import com.wafflestudio.snutt2.data.lectures.LectureRepository
import com.wafflestudio.snutt2.data.lectures.LectureRepositoryImpl
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepositoryImpl
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.tables.TableRepositoryImpl
import com.wafflestudio.snutt2.data.search_tags.SearchTagRepository
import com.wafflestudio.snutt2.data.search_tags.SearchTagRepositoryImpl
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindsCourseBookRepository(impl: CourseBookRepositoryImpl): CourseBookRepository

    @Binds
    abstract fun bindsCurrentTableRepository(impl: CurrentTableRepositoryImpl): CurrentTableRepository

    @Binds
    abstract fun bindsLectureRepository(impl: LectureRepositoryImpl): LectureRepository

    @Binds
    abstract fun bindsNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    abstract fun bindsTableRepository(impl: TableRepositoryImpl): TableRepository

    @Binds
    abstract fun bindsSearchTagRepository(impl: SearchTagRepositoryImpl): SearchTagRepository

    @Binds
    abstract fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
}
