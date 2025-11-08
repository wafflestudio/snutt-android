@Preview(showBackground = true)
@Composable
private fun RequestMethodListBottomSheetPreview() {
    AddFriendMethodListBottomSheet(
        onRequestWithNickName = {},
        onRequestWithKakaoTalk = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun RequestWithNicknameBottomSheetPreview() {
    RequestWithNicknameBottomSheet(
        initialNickname = "",
        onSubmit = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun RequestWithNicknameBottomSheetFilledPreview() {
    RequestWithNicknameBottomSheet(
        initialNickname = "홍길동#1234",
        onSubmit = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun FriendDetailBottomSheetPreview() {
    FriendDetailBottomSheet(
        friend = PreviewData.sampleFriends.first(),
        onEditDisplayName = {},
        onDeleteFriend = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun EditDisplayNameBottomSheetPreview() {
    EditDisplayNameBottomSheet(
        friend = PreviewData.sampleFriends.first(),
        initialDisplayName = "김철수",
        onSubmit = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun EditDisplayNameBottomSheetEmptyPreview() {
    EditDisplayNameBottomSheet(
        friend = PreviewData.sampleFriends[1],
        initialDisplayName = "",
        onSubmit = {},
        onDismiss = {},
    )
}
