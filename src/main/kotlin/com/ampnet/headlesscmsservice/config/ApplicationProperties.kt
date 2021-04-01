package com.ampnet.headlesscmsservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "com.ampnet.headlesscmsservice")
class ApplicationProperties {
    val jwt: JwtProperties = JwtProperties()
}

class JwtProperties {
    lateinit var publicKey: String
}
