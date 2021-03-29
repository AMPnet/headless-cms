package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.InvalidRequestException
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class StringToMailTypeConverter : Converter<String, MailType> {
    override fun convert(source: String): MailType {
        try {
            return MailType.valueOf(source.toUpperCase())
        } catch (ex: IllegalArgumentException) {
            throw InvalidRequestException(
                ErrorCode.CMS_MAIL_TYPE_UNDEFINED,
                "$source is not a valid mail type"
            )
        }
    }
}

@Component
class StringToLangConverter : Converter<String, Lang> {
    override fun convert(source: String): Lang {
        try {
            return Lang.valueOf(source.toUpperCase())
        } catch (ex: IllegalArgumentException) {
            throw InvalidRequestException(
                ErrorCode.CMS_LANGUAGE_UNDEFINED,
                "$source is not a valid language"
            )
        }
    }
}
