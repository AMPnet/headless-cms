package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.controller.pojo.MailUpdateRequest
import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.enums.lowerCaseName
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.persistence.model.Mail
import com.ampnet.headlesscmsservice.security.WithMockCrowdfundUser
import com.ampnet.headlesscmsservice.service.pojo.MailListResponse
import com.ampnet.headlesscmsservice.service.pojo.MailResponse
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MailControllerTest : ControllerTestBase() {

    private val translations by lazy {
        val json = javaClass.classLoader.getResource("mail_translations.json")?.readText()
            ?: fail("Failed to load mail_translations.json")
        objectMapper.readValue<Map<String, Map<String, String>>>(json)
    }

    private lateinit var testContext: TestContext

    private val invitationMailContent = "<p>You {{organization}} </p>\n\n<p>To review invite, " +
        "please follow the link: <a href=\"{{& link}}\">{{& link}}</a></p>"
    private val resetPasswordMailContent = "<p>To set new password, please follow the link: <a href=\"{{& link}}\">{{& link}}</a> " +
        "</p>\n<p>If you did not request a change of password, ignore this mail.</p>"

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
                    .param("type", MailType.INVITATION_MAIL.lowerCaseName())
                    .param("lang", Lang.EN.lowerCaseName())
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.mails).hasSize(1)
            val mail = response.mails.first()
            assertThat(mail.uuid).isEqualTo(testContext.mail.uuid)
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
    fun mustGetAllMailsByLanguage() {
        verify("Controller will return all mails by language") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("lang", Lang.EN.lowerCaseName())
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.mails).hasSize(MailType.values().size)
        }
    }

    @Test
    fun mustGetCustomMailsAndDefaultValues() {
        suppose("There are two custom mails") {
            val invitationMail = createMail(
                "Invitation", invitationMailContent, MailType.INVITATION_MAIL, Lang.EN, COOP
            )
            val resetPasswordMail = createMail(
                "Reset password", resetPasswordMailContent, MailType.RESET_PASSWORD_MAIL, Lang.EN, COOP
            )
            testContext.mails = listOf(invitationMail, resetPasswordMail)
        }

        verify("Controller will return mails by language") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("lang", Lang.EN.lowerCaseName())
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            val mails = response.mails.associateBy { it.type }
            assertThat(mails).hasSize(MailType.values().size)
            assertThat(mails[MailType.INVITATION_MAIL]?.content).isEqualTo(testContext.mails.first().content)
            assertThat(mails[MailType.RESET_PASSWORD_MAIL]?.content).isEqualTo(testContext.mails.last().content)
            mails.forEach { (type, mail) ->
                if (type == MailType.INVITATION_MAIL || type == MailType.RESET_PASSWORD_MAIL) {
                    assertThat(mail.uuid).isNotNull
                } else {
                    assertThat(mail.uuid).isNull()
                }
            }
        }
    }

    @Test
    fun mustGetAllEmailTranslationsByType() {
        verify("Controller will return mail all mails by language") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("type", MailType.INVITATION_MAIL.lowerCaseName())
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.mails).hasSize(Lang.values().size)
        }
    }

    @Test
    fun mustBeAbleToGetDefaultEmail() {
        verify("Controller will return default email") {
            val result = mockMvc.perform(
                get("/mail/$COOP")
                    .param("type", MailType.INVITATION_MAIL.lowerCaseName())
                    .param("lang", Lang.EN.lowerCaseName())
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.mails).hasSize(1)
            val mail = response.mails.first()
            val defaultInvitationMail = getDefaultMail(MailType.INVITATION_MAIL, Lang.EN, COOP)
            assertThat(mail.uuid).isNull()
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

    @Test
    @WithMockCrowdfundUser
    fun mustBeAbleUpdateMail() {
        val request = MailUpdateRequest("Invitation New Title", invitationMailContent)
        verify("Controller will return updated mail") {
            val result = mockMvc.perform(
                post(
                    "/mail/$COOP/${MailType.INVITATION_MAIL.lowerCaseName()}" +
                        "/${Lang.EN.lowerCaseName()}"
                )
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: MailResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.coop).isEqualTo(COOP)
            assertThat(response.title).isEqualTo(request.title)
            assertThat(response.content).isEqualTo(request.content)
            assertThat(response.type).isEqualTo(MailType.INVITATION_MAIL)
            assertThat(response.requiredFields).isEqualTo(MailType.INVITATION_MAIL.getRequiredFields().map { it.value })
            assertThat(response.lang).isEqualTo(Lang.EN)
        }
        verify("Mail is saved in the database") {
            val updatedMail = mailRepository.findByCoopAndOptionalTypeAndOptionalLang(COOP, MailType.INVITATION_MAIL, Lang.EN).first()
            assertThat(updatedMail.coop).isEqualTo(COOP)
            assertThat(updatedMail.title).isEqualTo(request.title)
            assertThat(updatedMail.content).isEqualTo(request.content)
            assertThat(updatedMail.type).isEqualTo(MailType.INVITATION_MAIL)
            assertThat(updatedMail.lang).isEqualTo(Lang.EN)
        }
    }

    @Test
    @WithMockCrowdfundUser(coop = "another-coop")
    fun mustThrowExceptionIfAdminIsFromAnotherCoop() {
        verify("Controller will return bad request") {
            val request = MailUpdateRequest("Invitation New Title", invitationMailContent)
            val result = mockMvc.perform(
                post("/mail/$COOP/${MailType.INVITATION_MAIL.lowerCaseName()}/${Lang.EN.lowerCaseName()}")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isBadRequest)
                .andReturn()
            verifyResponseErrorCode(result, ErrorCode.USER_MISSING_PRIVILEGE)
        }
    }

    private fun getDefaultMail(mailType: MailType, lang: Lang, coop: String): MailResponse {
        val content = translations[mailType.defaultTemplateKey]?.get(lang.lowerCaseName()) ?: fail("no default content")
        val title = translations[mailType.defaultTitleKey]?.get(lang.lowerCaseName()) ?: fail("no default title")
        return MailResponse(null, coop, title, content, mailType, mailType.getRequiredFields().map { it.value }, lang)
    }

    private class TestContext {
        lateinit var mail: Mail
        lateinit var mails: List<Mail>
    }
}
