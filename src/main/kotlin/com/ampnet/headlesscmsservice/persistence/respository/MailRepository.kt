package com.ampnet.headlesscmsservice.persistence.respository

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import com.ampnet.headlesscmsservice.persistence.model.Mail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface MailRepository : JpaRepository<Mail, UUID> {

    @Query(
        "SELECT mail FROM Mail mail WHERE mail.coop = :coop " +
            "AND (:type IS NULL OR mail.type = :type) " +
            "AND (:lang IS NULL OR mail.lang = :lang)"
    )
    fun findByCoopAndOptionalTypeAndOptionalLang(coop: String, type: MailType?, lang: Lang?): List<Mail>
}
