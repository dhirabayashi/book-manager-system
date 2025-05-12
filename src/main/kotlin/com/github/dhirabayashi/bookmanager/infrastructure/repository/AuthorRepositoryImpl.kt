package com.github.dhirabayashi.bookmanager.infrastructure.repository

import com.github.dhirabayashi.bookmanager.domain.IdGenerator
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.model.DraftAuthor
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
        return create.select(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .from(AUTHORS)
            .orderBy(AUTHORS.ID)
            .fetch().map { toModel(it) }
    }

    override fun add(author: DraftAuthor): Author {
        val record = create.newRecord(AUTHORS).also {
            it.id = idGenerator.generate()
            it.name = author.name
            it.birthDate = author.birthDate
            it.store()
        }
        return Author.create(record.id, record.name, record.birthDate)
    }

    override fun update(author: Author): Author? {
        // 更新実行
        val updatedCount = create.update(AUTHORS)
            .set(AUTHORS.NAME, author.name)
            .set(AUTHORS.BIRTH_DATE, author.birthDate)
            .where(AUTHORS.ID.eq(author.id))
            .execute()

        return if (updatedCount == 0) {
            null
        } else {
            Author.create(
                id = author.id,
                name = author.name,
                birthDate = author.birthDate,
            )
        }
    }

    override fun findByIds(ids: List<String>): List<Author> {
        return create.select(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .from(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .orderBy(AUTHORS.ID)
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
