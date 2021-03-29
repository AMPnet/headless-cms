package com.ampnet.headlesscmsservice.service.pojo

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType

class MailUpdateServiceRequest(
    val coop: String,
    val type: MailType,
    val lang: Lang,
    val title: String,
    val content: String
)
