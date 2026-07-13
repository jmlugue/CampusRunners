package com.malayanquest.app.ui.components

import androidx.compose.runtime.*
import com.malayanquest.app.ApiClient
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun RemoteList(
    api: ApiClient,
    endpoint: String,
    params: Map<String, String>,
    emptyText: String,
    refreshKey: Int = 0,
    itemContent: @Composable (JSONObject) -> Unit
) {
    var loading by remember(endpoint, params.toString(), refreshKey) { mutableStateOf(true) }
    var error by remember(endpoint, params.toString(), refreshKey) { mutableStateOf("") }
    var data by remember(endpoint, params.toString(), refreshKey) { mutableStateOf(JSONArray()) }

    LaunchedEffect(endpoint, params.toString(), refreshKey) {
        loading = true
        api.get(endpoint, HashMap(params)) { response ->
            loading = false
            if (!response.optBoolean("success")) {
                error = response.optString("message")
                data = JSONArray()
            } else {
                error = ""
                data = response.optJSONArray("data") ?: JSONArray()
            }
        }
    }

    when {
        loading -> InfoPanel("Loading records...")
        error.isNotBlank() -> InfoPanel(error)
        data.length() == 0 -> EmptyState(emptyText)
        else -> {
            for (i in 0 until data.length()) {
                data.optJSONObject(i)?.let { itemContent(it) }
            }
        }
    }
}

@Composable
fun RemoteObject(
    api: ApiClient,
    endpoint: String,
    params: Map<String, String>,
    emptyText: String,
    refreshKey: Int = 0,
    content: @Composable (JSONObject) -> Unit
) {
    var loading by remember(endpoint, params.toString(), refreshKey) { mutableStateOf(true) }
    var error by remember(endpoint, params.toString(), refreshKey) { mutableStateOf("") }
    var data by remember(endpoint, params.toString(), refreshKey) { mutableStateOf<JSONObject?>(null) }

    LaunchedEffect(endpoint, params.toString(), refreshKey) {
        loading = true
        api.get(endpoint, HashMap(params)) { response ->
            loading = false
            if (!response.optBoolean("success")) {
                error = response.optString("message")
                data = null
            } else {
                error = ""
                data = response.optJSONObject("data")
            }
        }
    }

    when {
        loading -> InfoPanel("Loading records...")
        error.isNotBlank() -> InfoPanel(error)
        data == null -> EmptyState(emptyText)
        else -> content(data!!)
    }
}
