package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.controller.pojo.ContentUpdateRequest
import com.ampnet.headlesscmsservice.exception.ErrorCode
import com.ampnet.headlesscmsservice.persistence.model.Content
import com.ampnet.headlesscmsservice.security.WithMockCrowdfundUser
import com.ampnet.headlesscmsservice.service.pojo.ContentListResponse
import com.ampnet.headlesscmsservice.service.pojo.ContentResponse
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContentControllerTest : ControllerTestBase() {

    private lateinit var testContext: TestContext

    private val key = "content_key"
    private val lang = "en"

    @BeforeEach
    fun init() {
        databaseCleanerService.deleteAllContent()
        testContext = TestContext()
    }

    @Test
    fun mustBeAbleToGetContent() {
        suppose("Content exists") {
            testContext.content = createContent("New Text", "content_key", "en", COOP)
        }

        verify("Controller will return content for coop, key and lang") {
            val result = mockMvc.perform(
                get("/text/$COOP")
                    .param("key", testContext.content.key)
                    .param("lang", testContext.content.lang)
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: ContentListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.contents).hasSize(1)
            val text = response.contents.first()
            assertThat(text.uuid).isEqualTo(testContext.content.uuid)
            assertThat(text.coop).isEqualTo(testContext.content.coop)
            assertThat(text.key).isEqualTo(testContext.content.key)
            assertThat(text.lang).isEqualTo(testContext.content.lang)
            assertThat(text.text).isEqualTo(testContext.content.text)
        }
    }

    @Test
    @WithMockCrowdfundUser
    fun mustBeAbleUpdateContent() {
        suppose("Content exists") {
            testContext.content = createContent("Some text", "content_key", "en", COOP)
        }

        verify("Controller will return updated content") {
            testContext.contentUpdateRequest = ContentUpdateRequest("Updated text")
            val result = mockMvc.perform(
                post("/text/$COOP/${testContext.content.key}/${testContext.content.lang}")
                    .content(objectMapper.writeValueAsString(testContext.contentUpdateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andReturn()
            val content: ContentResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(content.uuid).isEqualTo(testContext.content.uuid)
            assertThat(content.coop).isEqualTo(testContext.content.coop)
            assertThat(content.key).isEqualTo(testContext.content.key)
            assertThat(content.lang).isEqualTo(testContext.content.lang)
            assertThat(content.text).isEqualTo(testContext.contentUpdateRequest.text)
        }
        verify("Content is saved in the database") {
            val updatedText = contentRepository.findByCoopAndOptionalKeyAndOptionalLang(
                COOP, testContext.content.key, testContext.content.lang
            ).first()
            assertThat(updatedText.uuid).isEqualTo(testContext.content.uuid)
            assertThat(updatedText.coop).isEqualTo(testContext.content.coop)
            assertThat(updatedText.key).isEqualTo(testContext.content.key)
            assertThat(updatedText.lang).isEqualTo(testContext.content.lang)
            assertThat(updatedText.text).isEqualTo(testContext.contentUpdateRequest.text)
        }
    }

    @Test
    @WithMockCrowdfundUser
    fun mustBeAbleToSaveContentIfItDoesNotExist() {
        verify("Controller will return saved content") {
            testContext.contentUpdateRequest = ContentUpdateRequest("Some text")
            val result = mockMvc.perform(
                post("/text/$COOP/$key/$lang")
                    .content(objectMapper.writeValueAsString(testContext.contentUpdateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andReturn()
            val content: ContentResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(content.coop).isEqualTo(COOP)
            assertThat(content.key).isEqualTo(key)
            assertThat(content.lang).isEqualTo(lang)
            assertThat(content.text).isEqualTo(testContext.contentUpdateRequest.text)
        }
        verify("Content is saved in the database") {
            val updatedText = contentRepository.findByCoopAndOptionalKeyAndOptionalLang(COOP, key, lang).first()
            assertThat(updatedText.coop).isEqualTo(COOP)
            assertThat(updatedText.key).isEqualTo(key)
            assertThat(updatedText.lang).isEqualTo(lang)
            assertThat(updatedText.text).isEqualTo(testContext.contentUpdateRequest.text)
        }
    }

    @Test
    @WithMockCrowdfundUser(coop = "another-coop")
    fun mustThrowExceptionIfAdminIsFromAnotherCoop() {
        val request = ContentUpdateRequest("Updated text")
        verify("Controller will return bad request") {
            val result = mockMvc.perform(
                post("/text/$COOP/key/en")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isBadRequest)
                .andReturn()
            verifyResponseErrorCode(result, ErrorCode.USER_MISSING_PRIVILEGE)
        }
    }

    private class TestContext {
        lateinit var content: Content
        lateinit var contentUpdateRequest: ContentUpdateRequest
    }
}
