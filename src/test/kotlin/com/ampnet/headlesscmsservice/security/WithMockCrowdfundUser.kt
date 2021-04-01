package com.ampnet.headlesscmsservice.security

import com.ampnet.headlesscmsservice.controller.COOP
import com.ampnet.headlesscmsservice.enums.PrivilegeType
import com.ampnet.headlesscmsservice.enums.UserRole
import org.springframework.security.test.context.support.WithSecurityContext

@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@WithSecurityContext(factory = WithMockUserSecurityFactory::class)
annotation class WithMockCrowdfundUser(
    val uuid: String = "8a733721-9bb3-48b1-90b9-6463ac1493eb",
    val email: String = "user@email.com",
    val role: UserRole = UserRole.ADMIN,
    val privileges: Array<PrivilegeType> = [],
    val enabled: Boolean = true,
    val verified: Boolean = true,
    val coop: String = COOP
)
