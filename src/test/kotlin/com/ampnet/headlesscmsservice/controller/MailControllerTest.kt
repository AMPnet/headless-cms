package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.persistence.model.Mail
import com.ampnet.headlesscmsservice.service.pojo.MailListResponse
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MailControllerTest : ControllerTestBase() {

    private lateinit var testContext: TestContext

    private val invitationMailContent = "<p>You {{organization}} </p>\n\n<p>To review invite, " +
        "please follow the link: <a href=\"{{& link}}\">{{& link}}</a></p>"
    private val mailConfirmationContent = "<h2>Please confirmation your email</h2>\n\n<p>Follow the " +
        "link the confirm your email: <a href=\"{{& link}}\">{{& link}}</a></p>"

    @BeforeEach
    fun init() {
        databaseCleanerService.deleteAllEmails()
        testContext = TestContext()
    }

    @Test
    fun mustBeAbleToGetMail() {
        suppose("Email exists") {
            testContext.mail = createMail(
                "Invitation", invitationMailContent, MailType.INVITATION_MAIL, Lang.ENG
            )
        }

        verify("Controller will return mail for coop, type and lang") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("type", MailType.INVITATION_MAIL.toString())
                    .param("lang", Lang.ENG.toString())
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.mails).hasSize(1)
            val mail = response.mails.first()
            assertThat(mail.id).isEqualTo(testContext.mail.id)
            assertThat(mail.coop).isEqualTo(testContext.mail.coop)
            assertThat(mail.title).isEqualTo(testContext.mail.title)
            assertThat(mail.content).isEqualTo(testContext.mail.content)
            assertThat(mail.type).isEqualTo(testContext.mail.type)
            assertThat(mail.requiredFields).isEqualTo(
                testContext.mail.type.getRequiredFields().map { it.value }
            )
            assertThat(mail.lang).isEqualTo(testContext.mail.lang)
        }
    }

    @Test
    fun mustBeAbleToGetAllMailsForCoop() {
        suppose("Emails exists") {
            val invitationMail = createMail(
                "Invitation", invitationMailContent, MailType.INVITATION_MAIL, Lang.ENG
            )
            val confirmationMail = createMail(
                "Invitation", mailConfirmationContent, MailType.MAIL_CONFIRMATION_MAIL, Lang.ENG
            )
            testContext.mails = listOf(invitationMail, confirmationMail)
        }

        verify("Controller will return all mails for coop") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.mails).hasSize(2)
        }
    }

    @Test
    fun mustThrowExceptionForInvalidMailType() {
        verify("Controller will throw exception for invalid mail type") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("type", "Invalid_Mail_Type")
            )
                .andExpect(status().isBadRequest)
                .andReturn()
            verifyResponseErrorCode(result, ErrorCode.CMS_MAIL_TYPE_UNDEFINED)
        }
    }

    @Test
    fun mustThrowExceptionForInvalidLanguage() {
        verify("Controller will throw exception for invalid language") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("lang", "Invalid_Language")
            )
                .andExpect(status().isBadRequest)
                .andReturn()
            verifyResponseErrorCode(result, ErrorCode.CMS_LANGUAGE_UNDEFINED)
        }
    }

    private class TestContext {
        lateinit var mail: Mail
        lateinit var mails: List<Mail>
    }
}
