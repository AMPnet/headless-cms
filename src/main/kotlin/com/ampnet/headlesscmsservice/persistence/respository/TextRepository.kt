package com.ampnet.headlesscmsservice.persistence.respository

import com.ampnet.headlesscmsservice.persistence.model.Text
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface TextRepository : JpaRepository<Text, UUID> {

    @Query(
        "SELECT text FROM Text text WHERE text.coop = :coop " +
            "AND (:key IS NULL OR text.key = :key) " +
            "AND (:lang IS NULL OR text.lang = :lang)"
    )
    fun findByCoopAndOptionalKeyAndOptionalLang(coop: String, key: String?, lang: String?): List<Text>
}
