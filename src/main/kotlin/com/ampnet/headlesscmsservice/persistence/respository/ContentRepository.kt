package com.ampnet.headlesscmsservice.persistence.respository

import com.ampnet.headlesscmsservice.persistence.model.Content
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ContentRepository : JpaRepository<Content, UUID> {

    @Query(
        "SELECT content FROM Content content WHERE content.coop = :coop " +
            "AND (:key IS NULL OR content.key = :key) " +
            "AND (:lang IS NULL OR content.lang = :lang)"
    )
    fun findByCoopAndOptionalKeyAndOptionalLang(coop: String, key: String?, lang: String?): List<Content>
}
