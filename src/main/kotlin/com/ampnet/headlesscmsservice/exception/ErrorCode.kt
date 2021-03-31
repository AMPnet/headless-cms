package com.ampnet.headlesscmsservice.exception

enum class ErrorCode(val categoryCode: String, var specificCode: String, var message: String) {

    // 03
    USER_MISSING_PRIVILEGE("03", "05", "Missing privilege to access data"),

    // Internal: 08
    INT_CMS_DEFAULT_MAIL("08", "12", "Failed to get default mail translations"),

    // Cms: 12
    CMS_MAIL_MISSING("12", "01", "Mail missing"),
    CMS_REQUIRED_FIELD_MISSING("12", "02", "Required field in mail is missing"),
    CMS_MAIL_TYPE_UNDEFINED("12", "03", "Mail type is not defined"),
    CMS_LANGUAGE_UNDEFINED("12", "04", "Language is not defined");
}
