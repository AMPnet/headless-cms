package com.ampnet.headlesscmsservice.service.pojo

import com.ampnet.headlesscmsservice.persistence.model.Text
import java.util.UUID

data class TextResponse(
    val uuid: UUID,
    val coop: String,
    val key: String,
    val lang: String,
    val text: String,
) {
    constructor(text: Text) : this(
        text.uuid,
        text.coop,
        text.key,
        text.lang,
        text.text
    )
}

data class TextListResponse(
    val texts: List<TextResponse>
)
