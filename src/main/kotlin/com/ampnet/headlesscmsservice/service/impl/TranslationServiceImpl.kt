package com.ampnet.headlesscmsservice.service.impl

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.lowerCaseName
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.InternalException
import com.ampnet.headlesscmsservice.service.TranslationService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class TranslationServiceImpl(
    private val objectMapper: ObjectMapper
) : TranslationService {

    companion object : KLogging()

    private val translations by lazy {
        val json = javaClass.classLoader.getResource("mail_translations.json")?.readText()
            ?: throw InternalException(ErrorCode.INT_CMS_DEFAULT_MAIL, "Could not find mail_translations.json")
        objectMapper.readValue<Map<String, Map<String, String>>>(json)
    }

    override fun getTranslation(key: String, lang: Lang): String =
        translations[key]?.get(lang.lowerCaseName()) ?: translations[key]?.get(Lang.EN.lowerCaseName())
            ?: throw InternalException(
                ErrorCode.INT_CMS_DEFAULT_MAIL,
                "Could not find default[en] translation for $key"
            )
}
