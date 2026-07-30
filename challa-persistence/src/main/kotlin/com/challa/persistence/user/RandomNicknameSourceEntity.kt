package com.challa.persistence.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "random_nickname_source")
class RandomNicknameSourceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "type", nullable = false, length = 1)
    var type: String,

    @Column(name = "\"value\"", nullable = false)
    var value: String
) {
    companion object {
        const val TYPE_ADJECTIVE = "a"
        const val TYPE_NOUN = "b"
    }
}
