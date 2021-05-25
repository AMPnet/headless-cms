package com.ampnet.headlesscmsservice.controller

import com.ampnet.headlesscmsservice.controller.pojo.TextUpdateRequest
import com.ampnet.headlesscmsservice.persistence.model.Text
import com.ampnet.headlesscmsservice.security.WithMockCrowdfundUser
import com.ampnet.headlesscmsservice.service.pojo.TextListResponse
import com.ampnet.headlesscmsservice.service.pojo.TextResponse
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TextControllerTest : ControllerTestBase() {

    private lateinit var testContext: TestContext

    @BeforeEach
    fun init() {
        databaseCleanerService.deleteAllTexts()
        testContext = TestContext()
    }

    @Test
    fun mustBeAbleToGetText() {
        suppose("Text exists") {
            testContext.text = createText("New Text", "text_key", "en", COOP)
        }

        verify("Controller will return text for coop, key and lang") {
            val result = mockMvc.perform(
                get("/text/$COOP")
                    .param("key", testContext.text.key)
                    .param("lang", testContext.text.lang)
            )
                .andExpect(status().isOk)
                .andReturn()
            val response: TextListResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.texts).hasSize(1)
            val text = response.texts.first()
            assertThat(text.uuid).isEqualTo(testContext.text.uuid)
            assertThat(text.coop).isEqualTo(testContext.text.coop)
            assertThat(text.key).isEqualTo(testContext.text.key)
            assertThat(text.lang).isEqualTo(testContext.text.lang)
            assertThat(text.text).isEqualTo(testContext.text.text)
        }
    }

    @Test
    @WithMockCrowdfundUser
    fun mustBeAbleUpdateText() {
        suppose("Text exists") {
            testContext.text = createText("Some text", "text_key", "en", COOP)
        }

        verify("Controller will return updated text") {
            testContext.textUpdateRequest = TextUpdateRequest("Updated text")
            val result = mockMvc.perform(
                post("/text/$COOP/${testContext.text.key}/${testContext.text.lang}")
                    .content(objectMapper.writeValueAsString(testContext.textUpdateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andReturn()
            val text: TextResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(text.uuid).isEqualTo(testContext.text.uuid)
            assertThat(text.coop).isEqualTo(testContext.text.coop)
            assertThat(text.key).isEqualTo(testContext.text.key)
            assertThat(text.lang).isEqualTo(testContext.text.lang)
            assertThat(text.text).isEqualTo(testContext.textUpdateRequest.text)
        }
        verify("Text is saved in the database") {
            val updatedText = textRepository.findByCoopAndOptionalKeyAndOptionalLang(
                COOP, testContext.text.key, testContext.text.lang
            ).first()
            assertThat(updatedText.uuid).isEqualTo(testContext.text.uuid)
            assertThat(updatedText.coop).isEqualTo(testContext.text.coop)
            assertThat(updatedText.key).isEqualTo(testContext.text.key)
            assertThat(updatedText.lang).isEqualTo(testContext.text.lang)
            assertThat(updatedText.text).isEqualTo(testContext.textUpdateRequest.text)
        }
    }

    private class TestContext {
        lateinit var text: Text
        lateinit var textUpdateRequest: TextUpdateRequest
    }
}
