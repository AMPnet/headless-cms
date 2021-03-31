package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.persistence.model.Mail
import com.ampnet.headlesscmsservice.service.pojo.MailListResponse
import com.ampnet.headlesscmsservice.service.pojo.MailResponse
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class MailControllerTest : ControllerTestBase() {

    private val translations by lazy {
        val json = javaClass.classLoader.getResource("mail_translations.json")?.readText()
            ?: fail("Failed to load mail_translations.json")
        objectMapper.readValue<Map<String, Map<String, String>>>(json)
    }

    private lateinit var testContext: TestContext

    private val invitationMailContent = "<p>You {{organization}} </p>\n\n<p>To review invite, " +
        "please follow the link: <a href=\"{{& link}}\">{{& link}}</a></p>"

    @BeforeEach
    fun init() {
        databaseCleanerService.deleteAllEmails()
        testContext = TestContext()
    }

    @Test
    fun mustBeAbleToGetMail() {
        suppose("Email exists") {
            testContext.mail = createMail(
                "Invitation", invitationMailContent, MailType.INVITATION_MAIL, Lang.EN
            )
        }

        verify("Controller will return mail for coop, type and lang") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("type", MailType.INVITATION_MAIL.toString())
                    .param("lang", Lang.EN.toString())
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
    fun mustBeAbleToGetDefaultEmail() {
        verify("Controller will return default email") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("type", MailType.INVITATION_MAIL.toString())
                    .param("lang", Lang.EN.toString())
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.mails).hasSize(1)
            val mail = response.mails.first()
            val defaultInvitationMail = getDefaultMail(MailType.INVITATION_MAIL, Lang.EN, COOP)
            assertThat(mail.coop).isEqualTo(defaultInvitationMail.coop)
            assertThat(mail.title).isEqualTo(defaultInvitationMail.title)
            assertThat(mail.content).isEqualTo(defaultInvitationMail.content)
            assertThat(mail.type).isEqualTo(defaultInvitationMail.type)
            assertThat(mail.requiredFields).isEqualTo(
                defaultInvitationMail.type.getRequiredFields().map { it.value }
            )
            assertThat(mail.lang).isEqualTo(defaultInvitationMail.lang)
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

    private fun getDefaultMail(mailType: MailType, lang: Lang, coop: String): MailResponse {
        val content = translations[mailType.defaultTemplateKey]?.get(lang.name.toLowerCase()) ?: fail("no default content")
        val title = translations[mailType.defaultTitleKey]?.get(lang.name.toLowerCase()) ?: fail("no default title")
        return MailResponse(
            UUID.randomUUID(), coop, title, content, mailType,
            mailType.getRequiredFields().map { it.value }, lang
        )
    }

    private class TestContext {
        lateinit var mail: Mail
    }
}
