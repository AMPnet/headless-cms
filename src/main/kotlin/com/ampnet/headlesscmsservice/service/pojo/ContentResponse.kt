package com.ampnet.headlesscmsservice.service.pojo

import com.ampnet.headlesscmsservice.persistence.model.Content
import java.util.UUID

data class ContentResponse(
    val uuid: UUID,
    val coop: String,
    val key: String,
    val lang: String,
    val text: String,
) {
    constructor(content: Content) : this(
        content.uuid,
        content.coop,
        content.key,
        content.lang,
        content.text
    )
}

data class ContentListResponse(
    val contents: List<ContentResponse>
)
