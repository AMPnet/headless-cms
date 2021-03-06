package com.ampnet.headlesscmsservice.controller

import com.ampnet.core.jwt.UserPrincipal
import com.ampnet.headlesscmsservice.controller.pojo.ContentUpdateRequest
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.InvalidRequestException
import com.ampnet.headlesscmsservice.service.ContentService
import com.ampnet.headlesscmsservice.service.pojo.ContentListResponse
import com.ampnet.headlesscmsservice.service.pojo.ContentResponse
import com.ampnet.headlesscmsservice.service.pojo.ContentUpdateServiceRequest
import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ContentController(private val contentService: ContentService) {

    companion object : KLogging()

    @GetMapping("/text/{coop}")
    fun getContent(
        @PathVariable coop: String,
        @RequestParam(required = false) key: String?,
        @RequestParam(required = false) lang: String?
    ): ResponseEntity<ContentListResponse> {
        logger.debug {
            "Received request to get ${key ?: "all texts"} " +
                "in ${lang ?: "all languages"} for coop: $coop"
        }
        val text = contentService.findByCoop(coop, key, lang)
        return ResponseEntity.ok(text)
    }

    @PostMapping("/text/{coop}/{key}/{lang}")
    @PreAuthorize("hasAuthority(T(com.ampnet.headlesscmsservice.enums.PrivilegeType).PWA_COOP)")
    fun updateContent(
        @PathVariable coop: String,
        @PathVariable key: String,
        @PathVariable lang: String,
        @RequestBody request: ContentUpdateRequest
    ): ResponseEntity<ContentResponse> {
        logger.debug { "Received request to update $key in $lang for coop: $coop" }
        val userPrincipal = ControllerUtils.getUserPrincipalFromSecurityContext()
        throwExceptionIfUserIsMissingPrivilege(userPrincipal, coop)
        val serviceRequest = ContentUpdateServiceRequest(
            coop, key, lang, request.text
        )
        val updatedText = contentService.updateContent(serviceRequest)
        return ResponseEntity.ok(updatedText)
    }

    @DeleteMapping("/text/{coop}/{key}/{lang}")
    @PreAuthorize("hasAuthority(T(com.ampnet.headlesscmsservice.enums.PrivilegeType).PWA_COOP)")
    fun deleteContent(
        @PathVariable coop: String,
        @PathVariable key: String,
        @PathVariable lang: String,
    ): ResponseEntity<Unit> {
        logger.debug { "Received request to delete content by $key in $lang for coop: $coop" }
        val userPrincipal = ControllerUtils.getUserPrincipalFromSecurityContext()
        throwExceptionIfUserIsMissingPrivilege(userPrincipal, coop)
        contentService.deleteContent(coop, key, lang)
        return ResponseEntity.ok().build()
    }

    private fun throwExceptionIfUserIsMissingPrivilege(userPrincipal: UserPrincipal, coop: String) {
        if (userPrincipal.coop != coop)
            throw InvalidRequestException(
                ErrorCode.USER_MISSING_PRIVILEGE,
                "${userPrincipal.uuid} is not a member of this coop: $coop"
            )
    }
}
