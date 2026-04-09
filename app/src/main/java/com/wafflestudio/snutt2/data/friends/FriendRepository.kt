package com.wafflestudio.snutt2.data.friends

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.Friend
import com.wafflestudio.snutt2.domain.model.FriendState
import com.wafflestudio.snutt2.domain.model.Nickname
import com.wafflestudio.snutt2.domain.model.Table

interface FriendRepository {
    suspend fun getFriends(state: FriendState): Result<List<Friend>>

    suspend fun requestFriend(nickname: String): Result<Unit>

    suspend fun acceptFriend(friend: Friend): Result<Unit>

    suspend fun acceptFriendByLink(requestToken: String): Result<Nickname>

    suspend fun declineFriend(friend: Friend): Result<Unit>

    suspend fun deleteFriend(friend: Friend): Result<Unit>

    suspend fun patchFriendDisplayName(friend: Friend, displayName: String): Result<Unit>

    suspend fun generateFriendLink(): Result<String>

    suspend fun getFriendCourseBooks(friend: Friend): Result<List<CourseBook>>

    suspend fun getFriendPrimaryTable(friend: Friend, courseBook: CourseBook): Result<Table>
}
