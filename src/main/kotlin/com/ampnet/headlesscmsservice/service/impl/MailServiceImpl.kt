package com.ampnet.headlesscmsservice.service.impl

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailFieldName
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.InvalidRequestException
import com.ampnet.headlesscmsservice.exception.ResourceNotFoundException
import com.ampnet.headlesscmsservice.persistence.respository.MailRepository
import com.ampnet.headlesscmsservice.service.MailService
import com.ampnet.headlesscmsservice.service.pojo.MailListResponse
import com.ampnet.headlesscmsservice.service.pojo.MailResponse
import com.ampnet.headlesscmsservice.service.pojo.MailUpdateServiceRequest
import org.springframework.stereotype.Service

@Service
class MailServiceImpl(private val mailRepository: MailRepository) : MailService {

    override fun findByCoop(coop: String, type: MailType?, lang: Lang?): MailListResponse {
        val mails = mailRepository.findByCoopAndOptionalTypeAndOptionalLang(coop, type, lang)
            .map { MailResponse(it) }
        return MailListResponse(mails)
    }

    override fun updateMail(request: MailUpdateServiceRequest): MailResponse {
        val mail = mailRepository.findByCoopAndOptionalTypeAndOptionalLang(
            request.coop, request.type, request.lang
        ).firstOrNull() ?: throw ResourceNotFoundException(
            ErrorCode.CMS_MAIL_MISSING,
            "${request.type} in ${request.lang} for coop: ${request.coop} is missing"
        )
        validateMailUpdateHasRequiredFields(request.content, request.type.getRequiredFields())
        mail.title = request.title
        mail.content = request.content
        return MailResponse(mail)
    }

    private fun validateMailUpdateHasRequiredFields(mailContent: String, requiredFields: List<MailFieldName>) {
        val missingFields = requiredFields.filterNot { mailContent.contains(it.value, false) }
        if (missingFields.isNotEmpty()) throw InvalidRequestException(
            ErrorCode.CMS_REQUIRED_FIELD_MISSING,
            "Required fields are missing: ${ missingFields.joinToString() }"
        )
    }
}
