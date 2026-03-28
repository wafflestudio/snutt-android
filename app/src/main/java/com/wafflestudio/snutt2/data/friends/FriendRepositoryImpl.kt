package com.wafflestudio.snutt2.data.friends

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.FriendState
import com.wafflestudio.snutt2.domainmodel.Nickname
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PatchFriendDisplayNameParams
import com.wafflestudio.snutt2.lib.network.dto.PostRequestFriendParams
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.network.toDomainError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : FriendRepository {

    override suspend fun getFriends(state: FriendState): Result<List<Friend>> {
        try {
            val response = api._getFriends(state.name)
            return Result.Success(response.content.map { it.toDomainModel() })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun requestFriend(nickname: String): Result<Unit> {
        try {
            api._requestFriend(PostRequestFriendParams(nickname))
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun acceptFriend(friendId: String): Result<Unit> {
        try {
            api._acceptFriend(friendId)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun acceptFriendByLink(requestToken: String): Result<Nickname> {
        try {
            val result = api._acceptFriendByLink(requestToken)
            return Result.Success(result.nickname.toDomainModel())
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun declineFriend(friendId: String): Result<Unit> {
        try {
            api._declineFriend(friendId)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteFriend(friendId: String): Result<Unit> {
        try {
            api._deleteFriend(friendId)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun patchFriendDisplayName(friendId: String, displayName: String): Result<Unit> {
        try {
            api._patchFriendDisplayName(friendId, PatchFriendDisplayNameParams(displayName))
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun generateFriendLink(): Result<String> {
        try {
            val response = api._generateFriendLink()
            return Result.Success(response.requestToken)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getFriendCourseBooks(friendId: String): Result<List<CourseBook>> {
        try {
            val response = api._getFriendCourseBooks(friendId)
            return Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getFriendPrimaryTable(friendId: String, year: Int, semester: Int): Result<Table> {
        try {
            val timetableDto = api._getFriendPrimaryTable(friendId, semester.toString(), year)
            return Result.Success(Table.fromTimetableDto(timetableDto))
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
