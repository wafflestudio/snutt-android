package com.wafflestudio.snutt2.lib.network

sealed interface DomainError {
    val displayTitle: String
    val displayMessage: String
}

// 1. Global Errors (모든 API에서 발생 가능)
// Auth 관련 오류
sealed interface AuthError : DomainError
data class WrongApiKey(override val displayTitle: String, override val displayMessage: String) : AuthError
data class NoUserToken(override val displayTitle: String, override val displayMessage: String) : AuthError // 사실 발생하지 않는 오류일 수 있음. empty token을 보내도 WrongUserToken 발생.
data class WrongUserToken(override val displayTitle: String, override val displayMessage: String) : AuthError

// 기타 오류
data class EOF(override val displayTitle: String, override val displayMessage: String) : DomainError
data class NetworkDisconnect(override val displayTitle: String, override val displayMessage: String) : DomainError
data class ServerFault(override val displayTitle: String, override val displayMessage: String) : DomainError
data class NoAdminPrivilege(override val displayTitle: String, override val displayMessage: String) : DomainError
data class UnknownApp(override val displayTitle: String, override val displayMessage: String) : DomainError
data class Unknown(override val displayTitle: String, override val displayMessage: String) : DomainError
data class Nothing(override val displayTitle: String, override val displayMessage: String) : DomainError

// 2. API Errors (특정 API에서만 발생)
// 각 interface가 어떤 API인지를 의미하고, data class가 구체적인 오류 type을 명시한다.
// 각 data class가 여러 interface를 상속할 수 있어서, interface 밖에 선언되어 있다.
sealed interface SignupError : DomainError
sealed interface AddLocalIdError : DomainError
sealed interface ChangePasswordError : DomainError
sealed interface LectureReminderError : DomainError
sealed interface VacancyError : DomainError
sealed interface AddLectureError : DomainError

data class LectureOverlap(override val displayTitle: String, override val displayMessage: String) : AddLectureError
data class InvalidId(override val displayTitle: String, override val displayMessage: String) : SignupError, AddLocalIdError
data class InvalidPassword(override val displayTitle: String, override val displayMessage: String) : SignupError, AddLocalIdError, ChangePasswordError
data class DuplicateId(override val displayTitle: String, override val displayMessage: String) : SignupError, AddLocalIdError
data class UsedEmail(override val displayTitle: String, override val displayMessage: String) : SignupError
data class WrongPassword(override val displayTitle: String, override val displayMessage: String) : ChangePasswordError
data class PastSemester(override val displayTitle: String, override val displayMessage: String) : LectureReminderError
data class DisabledFeature(override val displayTitle: String, override val displayMessage: String) : VacancyError

// 3. Local Errors (클라이언트에서 처리하는 에러)
// 클라에서 발생하는 에러는 2가지뿐, 문자열을 하드코딩해 처리한다.
object TimetableLectureNotFound : DomainError {
    override val displayTitle = ""
    override val displayMessage = "시간표에서 삭제된 강좌입니다"
}

object BookmarkLectureNotFound : DomainError {
    override val displayTitle = ""
    override val displayMessage = "관심강좌 목록에서 삭제된 강좌입니다"
}

object NotSelectedTimetable : DomainError {
    override val displayTitle = ""
    // FIXME: 하드코딩 한국어. 실제로는 DisplayMessageResolverImpl에서 리소스로 대체되므로 사용되지 않지만,
    //  displayMessage를 직접 참조하는 코드가 추가될 경우 문제 발생 가능. DomainError를 @StringRes 기반으로 전환 검토.
    override val displayMessage = "현재 선택된 시간표의 테마만 변경할 수 있습니다."
}
