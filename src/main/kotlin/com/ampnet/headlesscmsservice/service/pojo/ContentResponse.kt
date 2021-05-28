package com.ampnet.headlesscmsservice.service.pojo

import com.ampnet.headlesscmsservice.persistence.model.Content

data class ContentResponse(
    val coop: String,
    val key: String,
    val lang: String,
    val text: String,
) {
    constructor(content: Content) : this(
        content.coop,
        content.key,
        content.lang,
        content.text
    )
}

data class ContentListResponse(
    val contents: List<ContentResponse>
)
