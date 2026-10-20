package com.example.uply_learntogether.data.remote

import com.google.gson.annotations.SerializedName


data class FactApiResponse(
    @SerializedName("ok") val ok: Boolean?,
    @SerializedName("facts") val facts: List<ApiFactDto>?
)

data class ApiFactDto(
    @SerializedName("id") val id: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("category") val category: String?
)
