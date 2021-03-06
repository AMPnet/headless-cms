package com.ampnet.headlesscmsservice.config

import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class DatabaseCleanerService(val em: EntityManager) {

    @Transactional
    fun deleteAllEmails() {
        em.createNativeQuery("TRUNCATE mail CASCADE").executeUpdate()
    }

    @Transactional
    fun deleteAllContent() {
        em.createNativeQuery("TRUNCATE content CASCADE").executeUpdate()
    }
}
