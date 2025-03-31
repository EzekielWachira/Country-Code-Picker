package com.ezzy.ccp.state

import android.util.Log.e

data class SearchState(
    val query: String = "",
    val countriesToShow: List<String> = emptyList()
)
