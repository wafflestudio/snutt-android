package com.wafflestudio.snutt2.di

import com.wafflestudio.snutt2.data.bookmark.BookmarkRepository
import com.wafflestudio.snutt2.data.bookmark.BookmarkRepositoryImpl
import com.wafflestudio.snutt2.data.coursebooks.CourseBookRepository
import com.wafflestudio.snutt2.data.coursebooks.CourseBookRepositoryImpl
import com.wafflestudio.snutt2.data.currenttablelecture.CurrentTableLectureRepository
import com.wafflestudio.snutt2.data.currenttablelecture.CurrentTableLectureRepositoryImpl
import com.wafflestudio.snutt2.data.friends.FriendRepository
import com.wafflestudio.snutt2.data.friends.FriendRepositoryImpl
import com.wafflestudio.snutt2.data.lecturediary.DiaryRepository
import com.wafflestudio.snutt2.data.lecturediary.DiaryRepositoryImpl
import com.wafflestudio.snutt2.data.lectureinfo.LectureInfoRepository
import com.wafflestudio.snutt2.data.lectureinfo.LectureInfoRepositoryImpl
import com.wafflestudio.snutt2.data.lecturesearch.LectureSearchRepository
import com.wafflestudio.snutt2.data.lecturesearch.LectureSearchRepositoryImpl
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepositoryImpl
import com.wafflestudio.snutt2.data.popup.PopupRepository
import com.wafflestudio.snutt2.data.popup.PopupRepositoryImpl
import com.wafflestudio.snutt2.data.semesterstatus.SemesterStatusRepository
import com.wafflestudio.snutt2.data.semesterstatus.SemesterStatusRepositoryImpl
import com.wafflestudio.snutt2.data.tabledisplay.TableDisplayRepository
import com.wafflestudio.snutt2.data.tabledisplay.TableDisplayRepositoryImpl
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.tables.TableRepositoryImpl
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepositoryImpl
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.user.UserRepositoryImpl
import com.wafflestudio.snutt2.data.vacancynoti.VacancyRepository
import com.wafflestudio.snutt2.data.vacancynoti.VacancyRepositoryImpl
import com.wafflestudio.snutt2.feature.debug.TestRepository
import com.wafflestudio.snutt2.feature.debug.TestRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindsBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    abstract fun bindsCourseBookRepository(impl: CourseBookRepositoryImpl): CourseBookRepository

    @Binds
    abstract fun bindsCurrentTableLectureRepository(impl: CurrentTableLectureRepositoryImpl): CurrentTableLectureRepository

    @Binds
    abstract fun bindsLectureRepository(impl: LectureSearchRepositoryImpl): LectureSearchRepository

    @Binds
    abstract fun bindsNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    abstract fun bindsPopupRepository(impl: PopupRepositoryImpl): PopupRepository

    @Binds
    abstract fun bindsTableDisplayRepository(impl: TableDisplayRepositoryImpl): TableDisplayRepository

    @Binds
    abstract fun bindsTableRepository(impl: TableRepositoryImpl): TableRepository

    @Binds
    abstract fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindsVacancyRepository(impl: VacancyRepositoryImpl): VacancyRepository

    @Binds
    abstract fun bindsThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

    @Binds
    abstract fun bindsTestRepository(impl: TestRepositoryImpl): TestRepository

    @Binds
    abstract fun bindsDiaryRepository(impl: DiaryRepositoryImpl): DiaryRepository

    @Binds
    abstract fun bindsSemesterStatusRepository(impl: SemesterStatusRepositoryImpl): SemesterStatusRepository

    @Binds
    abstract fun bindsFriendRepository(impl: FriendRepositoryImpl): FriendRepository

    @Binds
    abstract fun bindsLectureInfoRepository(impl: LectureInfoRepositoryImpl): LectureInfoRepository
}
