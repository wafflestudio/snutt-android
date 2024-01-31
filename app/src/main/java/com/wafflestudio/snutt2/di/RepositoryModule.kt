package com.wafflestudio.snutt2.di

import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.course_books.CourseBookRepositoryImpl
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepositoryImpl
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepositoryImpl
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepositoryImpl
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.tables.TableRepositoryImpl
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepositoryImpl
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.user.UserRepositoryImpl
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepositoryImpl
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
    abstract fun bindsLectureRepository(impl: LectureSearchRepositoryImpl): LectureSearchRepository

    @Binds
    abstract fun bindsNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    abstract fun bindsTableRepository(impl: TableRepositoryImpl): TableRepository

    @Binds
    abstract fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindsVacancyRepository(impl: VacancyRepositoryImpl): VacancyRepository

    @Binds
    abstract fun bindsThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository
}
