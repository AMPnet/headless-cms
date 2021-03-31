package com.ampnet.headlesscmsservice.service.impl

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailFieldName
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.InvalidRequestException
import com.ampnet.headlesscmsservice.exception.ResourceNotFoundException
import com.ampnet.headlesscmsservice.persistence.respository.MailRepository
import com.ampnet.headlesscmsservice.service.MailService
import com.ampnet.headlesscmsservice.service.TranslationService
import com.ampnet.headlesscmsservice.service.pojo.MailListResponse
import com.ampnet.headlesscmsservice.service.pojo.MailResponse
import com.ampnet.headlesscmsservice.service.pojo.MailTypeAndLang
import com.ampnet.headlesscmsservice.service.pojo.MailUpdateServiceRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MailServiceImpl(
    private val mailRepository: MailRepository,
    private val translationService: TranslationService
) : MailService {

    override fun findByCoop(coop: String, type: MailType?, lang: Lang?): MailListResponse {
        val customMails = mailRepository.findByCoopAndOptionalTypeAndOptionalLang(coop, type, lang)
            .map { MailResponse(it) }
        return when {
            (type != null && lang != null) -> {
                if (customMails.isNotEmpty()) MailListResponse(customMails)
                else MailListResponse(listOf(generateDefaultMail(coop, MailTypeAndLang(type, lang))))
            }
            (type == null && lang != null) -> getAllMailsByLanguage(customMails, coop, lang)
            (type != null && lang == null) -> getAllMailsByType(customMails, coop, type)
            else -> getAllMailsByTypeAndLanguage(customMails, coop)
        }
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

    private fun generateDefaultMail(
        coop: String,
        typeAndLang: MailTypeAndLang
    ): MailResponse {
        val content = translationService.getTranslation(typeAndLang.mailType.defaultTemplateKey, typeAndLang.lang)
        val title = translationService.getTranslation(typeAndLang.mailType.defaultTitleKey, typeAndLang.lang)
        return MailResponse(
            UUID.randomUUID(), coop, title, content, typeAndLang.mailType,
            typeAndLang.mailType.getRequiredFields().map { it.value }, typeAndLang.lang
        )
    }

    private fun getAllMailsByLanguage(
        customMails: List<MailResponse>,
        coop: String,
        lang: Lang
    ): MailListResponse {
        val mailByTypes = customMails.associateBy { it.type }.toMutableMap()
        MailType.values().forEach { mailType ->
            if (mailByTypes.containsKey(mailType).not()) {
                mailByTypes[mailType] = generateDefaultMail(coop, MailTypeAndLang(mailType, lang))
            }
        }
        return MailListResponse(mailByTypes.values.toList())
    }

    private fun getAllMailsByType(
        customMails: List<MailResponse>,
        coop: String,
        mailType: MailType
    ): MailListResponse {
        val mailByLanguages = customMails.associateBy { it.lang }.toMutableMap()
        Lang.values().forEach { lang ->
            if (mailByLanguages.containsKey(lang).not()) {
                mailByLanguages[lang] = generateDefaultMail(coop, MailTypeAndLang(mailType, lang))
            }
        }
        return MailListResponse(mailByLanguages.values.toList())
    }

    private fun getAllMailsByTypeAndLanguage(
        customMails: List<MailResponse>,
        coop: String
    ): MailListResponse {
        val typeAndLangList = MailType.values().flatMap {
            mailType ->
            Lang.values().map { lang -> MailTypeAndLang(mailType, lang) }
        }
        val mailsMap =
            customMails.associateBy { MailTypeAndLang(it.type, it.lang) }.toMutableMap()
        typeAndLangList.forEach { typeAndLang ->
            if (mailsMap.containsKey(typeAndLang).not()) {
                mailsMap[typeAndLang] = generateDefaultMail(coop, typeAndLang)
            }
        }
        return MailListResponse(mailsMap.values.toList())
    }
}
