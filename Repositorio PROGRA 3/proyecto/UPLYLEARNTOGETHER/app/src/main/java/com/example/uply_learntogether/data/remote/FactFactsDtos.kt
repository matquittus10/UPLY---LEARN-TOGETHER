package com.example.uply_learntogether.data.remote

import com.google.gson.annotations.SerializedName


data class FactResponse(
    @SerializedName("fact") val fact: List<FactDto>
)

data class FactDto(
    @SerializedName ("text") val text: String,
    @SerializedName ("category") val category: String
)