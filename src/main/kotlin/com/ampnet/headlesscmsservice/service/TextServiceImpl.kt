package com.ampnet.headlesscmsservice.service

import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.ResourceNotFoundException
import com.ampnet.headlesscmsservice.persistence.model.Text
import com.ampnet.headlesscmsservice.persistence.respository.TextRepository
import com.ampnet.headlesscmsservice.service.pojo.TextListResponse
import com.ampnet.headlesscmsservice.service.pojo.TextResponse
import com.ampnet.headlesscmsservice.service.pojo.TextUpdateServiceRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TextServiceImpl(private val textRepository: TextRepository) : TextService {

    @Transactional(readOnly = true)
    override fun findByCoop(coop: String, key: String?, languge: String?): TextListResponse {
        val texts = textRepository.findByCoopAndOptionalKeyAndOptionalLang(coop, key, languge)
        if (texts.isEmpty()) throw ResourceNotFoundException(
            ErrorCode.CMS_TEXT_NOT_FOUND, "Text for $key in $languge for $coop not found"
        )
        return TextListResponse(texts.map { TextResponse(it) })
    }

    @Transactional
    override fun updateText(request: TextUpdateServiceRequest): TextResponse {
        val text = textRepository.findByCoopAndOptionalKeyAndOptionalLang(
            request.coop, request.key, request.lang
        ).firstOrNull() ?: run {
            val text = textRepository.save(
                Text(UUID.randomUUID(), request.text, request.coop, request.key, request.lang)
            )
            return TextResponse(text)
        }
        text.text = request.text
        return TextResponse(text)
    }
}
