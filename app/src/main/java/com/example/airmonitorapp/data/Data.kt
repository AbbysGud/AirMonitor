package com.example.airmonitorapp.data

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("Data")
	val data: List<DataItem?>? = null
)

data class DataItem(

	@field:SerializedName("pm2_5")
	val pm25: String? = null,

	@field:SerializedName("co2")
	val co2: String? = null,

	@field:SerializedName("pm10")
	val pm100: String? = null,

	@field:SerializedName("suhu")
	val suhu: String? = null,

	@field:SerializedName("pm1_0")
	val pm10: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("kecerahan")
	val kecerahan: String? = null,

	@field:SerializedName("metana")
	val metana: String? = null,

	@field:SerializedName("kelembaban")
	val kelembaban: String? = null
)
