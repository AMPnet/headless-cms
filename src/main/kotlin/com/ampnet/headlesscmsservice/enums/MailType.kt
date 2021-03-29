package com.ampnet.headlesscmsservice.enums

enum class MailType(val id: Int) {

    INVITATION_MAIL(1) {
        override fun getRequiredFields(): List<MailFieldName> {
            return listOf(
                MailFieldName.ORGANIZATION,
                MailFieldName.LINK
            )
        }
    },

    MAIL_CONFIRMATION_MAIL(2) {
        override fun getRequiredFields(): List<MailFieldName> {
            return listOf(
                MailFieldName.LINK
            )
        }
    };

    companion object {
        private val map = values().associateBy(MailType::id)
        fun fromInt(type: Int) = map[type]
    }

    abstract fun getRequiredFields(): List<MailFieldName>
}
