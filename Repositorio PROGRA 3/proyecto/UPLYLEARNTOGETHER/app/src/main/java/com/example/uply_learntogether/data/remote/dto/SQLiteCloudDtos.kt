package com.example.uply_learntogether.data.remote.dto

data class SqlRequest(
    val sql: String
)

data class SqlResponse(
    val code: Int?,
    val data: List<Map<String, Any>>?,
    val error: String?
)
