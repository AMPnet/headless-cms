package com.ampnet.headlesscmsservice.service.pojo

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.persistence.model.Mail
import java.util.UUID

data class MailResponse(
    val id: UUID?,
    val coop: String,
    val title: String,
    val content: String,
    val type: MailType,
    val requiredFields: List<String>,
    val lang: Lang
) {
    constructor(mail: Mail) : this(
        mail.id,
        mail.coop,
        mail.title,
        mail.content,
        mail.type,
        mail.type.getRequiredFields().map { it.value },
        mail.lang,
    )
}

data class MailListResponse(
    val mails: List<MailResponse>
)
