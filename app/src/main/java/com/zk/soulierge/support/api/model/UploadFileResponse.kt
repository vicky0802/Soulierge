package com.zk.soulierge.support.api.model

import com.google.gson.annotations.SerializedName

class UploadFileResponse (@SerializedName("success")
                          var success: String? = null,
                          @SerializedName("file_name")
                          var file_name: String? = null,
                          @SerializedName("failure")
                          var failure: String? = null)
