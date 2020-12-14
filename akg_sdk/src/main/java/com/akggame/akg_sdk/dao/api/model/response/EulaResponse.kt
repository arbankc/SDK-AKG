package com.akggame.akg_sdk.dao.api.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EulaResponse(

	@field:JsonProperty("data")
	val data: DataEula? = null,

	@field:JsonProperty("meta")
	val meta: Meta? = null
) : Parcelable, BaseResponse()


@Parcelize
data class DataEula(

	@field:JsonProperty("attributes")
	val attributes: AttributesEula? = null,

	@field:JsonProperty("id")
	val id: String? = null,

	@field:JsonProperty("type")
	val type: String? = null
) : Parcelable

@Parcelize
data class AttributesEula(

	@field:JsonProperty("game_name")
	val gameName: String? = null,

	@field:JsonProperty("contents")
	val contents: String? = null
) : Parcelable
