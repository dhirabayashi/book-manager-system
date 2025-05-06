package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.IdGenerator
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import com.github.dhirabayashi.bookmanager.infrastructure.db.Tables.AUTHORS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class AuthorRepositoryImpl(
    private val create: DSLContext,
    private val idGenerator: IdGenerator,
) : AuthorRepository {
    override fun findAll(): List<Author> {
        return create.select()
            .from(AUTHORS)
            .fetch().map { toModel(it) }
    }

    override fun add(author: Author): Author {
        val record = create.newRecord(AUTHORS).also {
            it.id = author.id ?: idGenerator.generate()
            it.name = author.name
            it.birthDate = author.birthDate
            it.store()
        }
        return Author.create(record.id, record.name, record.birthDate)
    }

    override fun update(id: String, author: Author): Author? {
        // 更新実行
        val updatedCount = create.update(AUTHORS)
            .set(AUTHORS.NAME, author.name)
            .set(AUTHORS.BIRTH_DATE, author.birthDate)
            .where(AUTHORS.ID.eq(id))
            .execute()

        return if (updatedCount == 0) {
            null
        } else {
            Author.create(
                id = id,
                name = author.name,
                birthDate = author.birthDate,
            )
        }
    }

    override fun findByIds(ids: List<String>): List<Author> {
        return create.select()
            .from(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .fetch()
            .map { toModel(it) }
    }

    /**
     * 著者レコードを著者のドメインモデルに変換する
     *
     * @param record 著者レコード
     * @return ドメインモデル
     */
    private fun toModel(record: Record) = Author.create(
        record.getValue(AUTHORS.ID),
        record.getValue(AUTHORS.NAME),
        record.getValue(AUTHORS.BIRTH_DATE),
    )
}
