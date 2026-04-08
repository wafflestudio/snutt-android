package com.wafflestudio.snutt2.data.friends

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.FriendState
import com.wafflestudio.snutt2.domainmodel.Nickname
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.PatchFriendDisplayNameParams
import com.wafflestudio.snutt2.network.dto.PostRequestFriendParams
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.network.error.toDomainError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : FriendRepository {

    override suspend fun getFriends(state: FriendState): Result<List<Friend>> {
        try {
            val response = api._getFriends(state.name)
            return Result.Success(response.content.map { it.toDomain() })
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

    override suspend fun acceptFriend(friend: Friend): Result<Unit> {
        try {
            api._acceptFriend(friend.id)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun acceptFriendByLink(requestToken: String): Result<Nickname> {
        try {
            val result = api._acceptFriendByLink(requestToken)
            return Result.Success(result.nickname.toDomain())
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun declineFriend(friend: Friend): Result<Unit> {
        try {
            api._declineFriend(friend.id)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteFriend(friend: Friend): Result<Unit> {
        try {
            api._deleteFriend(friend.id)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun patchFriendDisplayName(friend: Friend, displayName: String): Result<Unit> {
        try {
            api._patchFriendDisplayName(friend.id, PatchFriendDisplayNameParams(displayName))
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

    override suspend fun getFriendCourseBooks(friend: Friend): Result<List<CourseBook>> {
        try {
            val response = api._getFriendCourseBooks(friend.id)
            return Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getFriendPrimaryTable(friend: Friend, courseBook: CourseBook): Result<Table> {
        try {
            val timetableDto = api._getFriendPrimaryTable(friend.id, courseBook.semester.toInt().toString(), courseBook.year.toInt())
            return Result.Success(timetableDto.toDomain())
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
