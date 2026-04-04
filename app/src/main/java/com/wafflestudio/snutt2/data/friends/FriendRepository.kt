package com.wafflestudio.snutt2.data.friends

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.FriendState
import com.wafflestudio.snutt2.domainmodel.Nickname
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.lib.network.Result

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
