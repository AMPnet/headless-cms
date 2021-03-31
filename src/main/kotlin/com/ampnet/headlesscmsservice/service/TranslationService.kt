package com.ampnet.headlesscmsservice.service

import com.ampnet.headlesscmsservice.enums.Lang

interface TranslationService {
    fun getTranslation(key: String, lang: Lang): String
}
