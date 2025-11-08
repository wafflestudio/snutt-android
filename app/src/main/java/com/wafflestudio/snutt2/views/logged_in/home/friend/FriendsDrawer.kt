@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
private fun FriendsDrawerContentPreview() {
    FriendsDrawerContent(
        uiState = PreviewData.sampleFriendsUiState,
        onClose = {},
        onSelectTab = {},
        onSelectFriend = {},
        onOpenAddFriendBottomSheet = {},
        onOpenFriendDetail = {},
        onAcceptFriend = {},
        onDeclineFriend = {},
    )
}
