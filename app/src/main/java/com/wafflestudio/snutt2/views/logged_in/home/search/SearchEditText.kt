package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.clearFocusOnKeyboardDismiss
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

@Composable
fun RowScope.SearchEditText(
    searchEditTextFocused: Boolean,
    onFocus: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val searchViewModel: SearchViewModel = hiltViewModel()
    val searchKeyword by searchViewModel.searchTitle.collectAsState()

    EditText(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .onFocusChanged { onFocus(it.isFocused) }
            .clearFocusOnKeyboardDismiss(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onFocus(false)
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    searchViewModel.query()
                }
            }
        }),
        value = searchKeyword,
        onValueChange = {
            scope.launch {
                searchViewModel.setTitle(it)
            }
        },
        singleLine = true,
        hint = stringResource(R.string.search_hint),
        underlineEnabled = false,
        clearFocusFlag = searchEditTextFocused.not(),
    )
}
