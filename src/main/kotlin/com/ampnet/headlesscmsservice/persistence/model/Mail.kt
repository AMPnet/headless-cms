package com.ampnet.headlesscmsservice.persistence.model

import com.ampnet.headlesscmsservice.enums.Lang
import com.ampnet.headlesscmsservice.enums.MailType
import javax.persistence.AttributeConverter
import javax.persistence.Column
import javax.persistence.Converter
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "mail")
class Mail(

    @Id
    val id: Int,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    val coop: String,

    @Column(name = "type_id", nullable = false)
    val type: MailType,

    @Enumerated(EnumType.STRING)
    @Column(name = "lang", nullable = false)
    val lang: Lang
) {
    constructor(
        title: String,
        content: String,
        coop: String,
        type: MailType,
        lang: Lang
    ) : this(
        MailId(coop, type, lang).hashCode(),
        title, content, coop, type, lang
    )
}

data class MailId(
    val coop: String,
    val type: MailType,
    val lang: Lang
)

@Converter(autoApply = true)
class MailTypeConverter : AttributeConverter<MailType, Int> {
    override fun convertToDatabaseColumn(attribute: MailType?): Int? =
        attribute?.id

    override fun convertToEntityAttribute(dbData: Int): MailType? =
        MailType.fromInt(dbData)
}
