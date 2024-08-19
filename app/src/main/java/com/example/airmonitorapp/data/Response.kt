package com.example.airmonitorapp.data

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("Response")
	val response: List<ResponseItem?>? = null
)

data class ResponseItem(

	@field:SerializedName("min")
	val min: String? = null,

	@field:SerializedName("avg")
	val avg: String? = null,

	@field:SerializedName("max")
	val max: String? = null
)
