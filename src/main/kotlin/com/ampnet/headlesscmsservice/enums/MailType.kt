package com.ampnet.headlesscmsservice.enums

@Suppress("MagicNumber")
enum class MailType(val id: Int, val defaultTemplateKey: String, val defaultTitleKey: String) {

    ACTIVATED_USER_WALLET_MAIL(
        1,
        "userWalletActivatedTemplate",
        "walletActivatedTitle"
    ) {
        override fun getRequiredFields(): List<MailFieldName> =
            listOf(MailFieldName.ACTIVATION_DATA, MailFieldName.LINK)
    },
    ACTIVATED_ORGANIZATION_WALLET_MAIL(
        2,
        "organizationWalletActivatedTemplate",
        "walletActivatedTitle"
    ) {
        override fun getRequiredFields(): List<MailFieldName> = listOf(MailFieldName.LINK)
    },
    ACTIVATED_PROJECT_WALLET_MAIL(
        3,
        "projectWalletActivatedTemplate",
        "walletActivatedTitle"
    ) {
        override fun getRequiredFields(): List<MailFieldName> =
            listOf(MailFieldName.PROJECT_NAME, MailFieldName.LINK)
    },
    MAIL_CONFIRMATION_MAIL(4, "mailConfirmationTemplate", "confirmationTitle") {
        override fun getRequiredFields(): List<MailFieldName> =
            listOf(MailFieldName.LINK)
    },
    DEPOSIT_INFO_MAIL(5, "depositTemplate", "depositInfoTitle") {
        // TODO think about different structure
        override fun getRequiredFields(): List<MailFieldName> = listOf()
    },
    DEPOSIT_REQUEST_MAIL(6, "depositRequestTemplate", "depositInfoTitle") {
        override fun getRequiredFields(): List<MailFieldName> = listOf(MailFieldName.AMOUNT)
    },
    FAILED_DELIVERY_MAIL(7, "failedDeliveryMessageTemplate", "failedDeliveryTitle") {
        override fun getRequiredFields(): List<MailFieldName> = listOf(MailFieldName.FAILED_RECIPIENTS)
    },
    INVITATION_MAIL(8, "invitationTemplate", "invitationTitle") {
        override fun getRequiredFields(): List<MailFieldName> =
            listOf(MailFieldName.ORGANIZATION, MailFieldName.LINK)
    },
    NEW_USER_WALLET_MAIL(9, "userWalletTemplate", "newWalletTitle") {
        override fun getRequiredFields(): List<MailFieldName> =
            listOf(MailFieldName.ORGANIZATION, MailFieldName.LINK)
    },
    NEW_PROJECT_WALLET_MAIL(10, "projectWalletTemplate", "newWalletTitle") {
        override fun getRequiredFields(): List<MailFieldName> = listOf(MailFieldName.LINK)
    },
    NEW_ORGANIZATION_WALLET_MAIL(11, "organizationWalletTemplate", "newWalletTitle") {
        override fun getRequiredFields(): List<MailFieldName> = listOf(MailFieldName.LINK)
    },
    PROJECT_FULLY_FUNDED_MAIL(12, "projectFullyFundedTemplate", "projectFullyFundedTitle") {
        override fun getRequiredFields(): List<MailFieldName> =
            listOf(MailFieldName.FIRST_NAME, MailFieldName.PROJECT_NAME, MailFieldName.LINK)
    },
    RESET_PASSWORD_MAIL(13, "forgotPasswordTemplate", "resetPasswordTitle") {
        override fun getRequiredFields(): List<MailFieldName> = listOf(MailFieldName.LINK)
    },
    SUCCESSFULLY_INVESTED_MAIL(14, "investmentTemplate", "investmentTitle") {
        // TODO think about different structure
        override fun getRequiredFields(): List<MailFieldName> = listOf()
    },
    WITHDRAW_INFO_MAIL(15, "withdrawTemplate", "withdrawTitle") {
        // TODO think about different structure
        override fun getRequiredFields(): List<MailFieldName> = listOf()
    },
    WITHDRAW_REQUEST_MAIL(16, "withdrawRequestTemplate", "withdrawTitle") {
        override fun getRequiredFields(): List<MailFieldName> = listOf(MailFieldName.AMOUNT)
    };

    companion object {
        private val map = values().associateBy(MailType::id)
        fun fromInt(type: Int) = map[type]
    }

    abstract fun getRequiredFields(): List<MailFieldName>
}
