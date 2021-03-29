package com.ampnet.headlesscmsservice.exception

class InvalidRequestException(
    val errorCode: ErrorCode,
    exceptionMessage: String,
    throwable: Throwable? = null,
    val errors: Map<String, String> = emptyMap()
) : Exception(exceptionMessage, throwable)

class ResourceNotFoundException(val errorCode: ErrorCode, exceptionMessage: String) : Exception(exceptionMessage)
