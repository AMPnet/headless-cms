package com.ampnet.headlesscmsservice.exception

data class ErrorResponse(
    val description: String,
    val errCode: String,
    val message: String,
    val errors: Map<String, String>?
)
