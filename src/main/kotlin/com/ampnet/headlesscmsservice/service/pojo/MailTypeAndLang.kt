package com.ampnet.headlesscmsservice.service.pojo

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType

data class MailTypeAndLang(
    val mailType: MailType,
    val lang: Lang
)
