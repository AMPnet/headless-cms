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
)

class MailId(
    val coop: String,
    var type: MailType,
    var lang: Lang
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MailId
        if (
            coop != other.coop ||
            type != other.type ||
            lang != other.lang
        ) return false
        return true
    }

    override fun hashCode(): Int =
        coop.hashCode() + type.hashCode() + lang.hashCode()
}

@Converter(autoApply = true)
class MailTypeConverter : AttributeConverter<MailType, Int> {
    override fun convertToDatabaseColumn(attribute: MailType?): Int? =
        attribute?.id

    override fun convertToEntityAttribute(dbData: Int): MailType? =
        MailType.fromInt(dbData)
}
