package com.ampnet.headlesscmsservice.service

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.service.pojo.MailListResponse
import com.ampnet.headlesscmsservice.service.pojo.MailResponse
import com.ampnet.headlesscmsservice.service.pojo.MailUpdateServiceRequest

interface MailService {
    fun findByCoop(coop: String, type: MailType?, lang: Lang?): MailListResponse
    fun updateMail(request: MailUpdateServiceRequest): MailResponse
}
