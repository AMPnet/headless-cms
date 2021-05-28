package com.ampnet.headlesscmsservice.persistence.model

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "content")
class Content(

    @Id
    @Column
    val uuid: UUID,

    @Column(nullable = false)
    var text: String,

    @Column(nullable = false)
    val coop: String,

    @Column(nullable = false)
    val key: String,

    @Column(nullable = false)
    val lang: String
)
