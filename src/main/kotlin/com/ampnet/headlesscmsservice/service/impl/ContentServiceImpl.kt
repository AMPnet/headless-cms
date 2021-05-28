package com.ampnet.headlesscmsservice.service.impl

import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.ResourceNotFoundException
import com.ampnet.headlesscmsservice.persistence.model.Content
import com.ampnet.headlesscmsservice.persistence.respository.ContentRepository
import com.ampnet.headlesscmsservice.service.ContentService
import com.ampnet.headlesscmsservice.service.pojo.ContentListResponse
import com.ampnet.headlesscmsservice.service.pojo.ContentResponse
import com.ampnet.headlesscmsservice.service.pojo.ContentUpdateServiceRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ContentServiceImpl(private val contentRepository: ContentRepository) : ContentService {

    @Transactional(readOnly = true)
    override fun findByCoop(coop: String, key: String?, language: String?): ContentListResponse {
        val texts = contentRepository.findByCoopAndOptionalKeyAndOptionalLang(coop, key, language)
        if (texts.isEmpty()) throw ResourceNotFoundException(
            ErrorCode.CMS_CONTENT_NOT_FOUND, "Text for $key in $language for $coop not found"
        )
        return ContentListResponse(texts.map { ContentResponse(it) })
    }

    @Transactional
    override fun updateContent(request: ContentUpdateServiceRequest): ContentResponse {
        val text = contentRepository.findByCoopAndOptionalKeyAndOptionalLang(
            request.coop, request.key, request.lang
        ).firstOrNull() ?: run {
            val text = contentRepository.save(
                Content(UUID.randomUUID(), request.text, request.coop, request.key, request.lang)
            )
            return ContentResponse(text)
        }
        text.text = request.text
        return ContentResponse(text)
    }
}
