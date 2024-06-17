package com.wafflestudio.snutt2.views.logged_in.home.search.bookmark

enum class SearchPageMode {
    Search,
    Bookmark,
    ;

    fun toggled(): SearchPageMode {
        return when (this) {
            Search -> Bookmark
            Bookmark -> Search
        }
    }
}
