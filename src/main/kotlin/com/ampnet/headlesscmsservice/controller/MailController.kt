package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.controller.pojo.MailUpdateRequest
import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.service.MailService
import com.ampnet.headlesscmsservice.service.pojo.MailListResponse
import com.ampnet.headlesscmsservice.service.pojo.MailResponse
import com.ampnet.headlesscmsservice.service.pojo.MailUpdateServiceRequest
import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MailController(private val mailService: MailService) {

    companion object : KLogging()

    @GetMapping("/mail/{coop}")
    fun getMail(
        @PathVariable coop: String,
        @RequestParam(required = false) type: MailType?,
        @RequestParam(required = false) lang: Lang?
    ): ResponseEntity<MailListResponse> {
        val mail = mailService.findByCoop(coop, type, lang)
        return ResponseEntity.ok(mail)
    }

    @PutMapping("/mail/{coop}/{type}/{lang}")
    fun updateMail(
        @PathVariable coop: String,
        @PathVariable type: MailType,
        @PathVariable lang: Lang,
        @RequestBody request: MailUpdateRequest
    ): ResponseEntity<MailResponse> {
        val serviceRequest = MailUpdateServiceRequest(
            coop, type, lang, request.title, request.content
        )
        val updatedMail = mailService.updateMail(serviceRequest)
        return ResponseEntity.ok(updatedMail)
    }
}
