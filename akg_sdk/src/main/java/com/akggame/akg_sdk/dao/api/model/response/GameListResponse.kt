package com.akggame.akg_sdk.dao.api.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameListResponse(

	@field:JsonProperty("data")
	val data: List<DataItemGameList?>? = null,

	@field:JsonProperty("meta")
	val meta: Meta? = null
) : Parcelable, BaseResponse()

@Parcelize
data class Attributes(

	@field:JsonProperty("revenue_share")
	val revenueShare: Int? = null,

	@field:JsonProperty("name")
	val name: String? = null,

	@field:JsonProperty("package_name")
	val packageName: String? = null,

	@field:JsonProperty("product_code")
	val productCode: String? = null,

	@field:JsonProperty("platform")
	val platform: String? = null
) : Parcelable

@Parcelize
data class Meta(

	@field:JsonProperty("code")
	val code: Int? = null,

	@field:JsonProperty("status")
	val status: Boolean? = null
) : Parcelable

@Parcelize
data class DataItemGameList(

	@field:JsonProperty("attributes")
	val attributes: Attributes? = null,

	@field:JsonProperty("id")
	val id: String? = null,

	@field:JsonProperty("type")
	val type: String? = null
) : Parcelable
