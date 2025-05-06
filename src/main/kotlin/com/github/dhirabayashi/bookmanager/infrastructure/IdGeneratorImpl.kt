package com.github.dhirabayashi.bookmanager.infrastructure

import com.github.dhirabayashi.bookmanager.domain.IdGenerator
import de.huxhorn.sulky.ulid.ULID
import org.springframework.stereotype.Component

@Component
class IdGeneratorImpl : IdGenerator {
    companion object {
        private val ulid = ULID()
    }

    override fun generate(): String {
        return ulid.nextULID()
    }
}
