package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.controller.pojo.TextUpdateRequest
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.exception.InvalidRequestException
import com.ampnet.headlesscmsservice.service.TextService
import com.ampnet.headlesscmsservice.service.pojo.TextListResponse
import com.ampnet.headlesscmsservice.service.pojo.TextResponse
import com.ampnet.headlesscmsservice.service.pojo.TextUpdateServiceRequest
import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TextController(private val textService: TextService) {

    companion object : KLogging()

    @GetMapping("/text/{coop}")
    fun getText(
        @PathVariable coop: String,
        @RequestParam(required = false) key: String?,
        @RequestParam(required = false) lang: String?
    ): ResponseEntity<TextListResponse> {
        logger.debug {
            "Received request to get ${key ?: "all texts"} " +
                "in ${lang ?: "all languages"} for coop: $coop"
        }
        val text = textService.findByCoop(coop, key, lang)
        return ResponseEntity.ok(text)
    }

    @PostMapping("/text/{coop}/{key}/{lang}")
    @PreAuthorize("hasAuthority(T(com.ampnet.headlesscmsservice.enums.PrivilegeType).PWA_COOP)")
    fun updateText(
        @PathVariable coop: String,
        @PathVariable key: String,
        @PathVariable lang: String,
        @RequestBody request: TextUpdateRequest
    ): ResponseEntity<TextResponse> {
        logger.debug { "Received request to update $key in $lang for coop: $coop" }
        val userPrincipal = ControllerUtils.getUserPrincipalFromSecurityContext()
        if (userPrincipal.coop != coop)
            throw InvalidRequestException(
                ErrorCode.USER_MISSING_PRIVILEGE,
                "${userPrincipal.uuid} is not a member of this coop: $coop"
            )
        val serviceRequest = TextUpdateServiceRequest(
            coop, key, lang, request.text
        )
        val updatedText = textService.updateText(serviceRequest)
        return ResponseEntity.ok(updatedText)
    }
}
