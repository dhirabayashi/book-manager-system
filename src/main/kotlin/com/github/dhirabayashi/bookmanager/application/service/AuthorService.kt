package com.github.dhirabayashi.bookmanager.application.service

import com.github.dhirabayashi.bookmanager.application.exception.EntityNotFoundException
import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import com.github.dhirabayashi.bookmanager.domain.check.validate
import com.github.dhirabayashi.bookmanager.domain.model.Author
import com.github.dhirabayashi.bookmanager.domain.reposiroty.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 著者のユースケース
 *
 * @property authorRepository 著者リポジトリ
 */
@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
) {
    /**
     * 全ての著者を取得する
     *
     * @return 全ての著者
     */
    @Transactional(readOnly = true)
    fun listAll(): List<AuthorDto> {
        return authorRepository.findAll()
            .map { AuthorDto.of(it) }
    }

    /**
     * 著者を登録する
     *
     * @param author 登録する著者
     * @return 登録した著者
     */
    @Transactional(rollbackFor = [Exception::class])
    fun add(author: Author): AuthorDto {
        return authorRepository.add(author)
            .let { AuthorDto.of(it) }
    }

    /**
     * 著者を更新する
     *
     * @param author 著者の更新データ
     * @return 更新後の著者
     * @throws ValidationException 著者IDが未指定の場合
     */
    @Transactional(rollbackFor = [Exception::class])
    fun update(author: Author): AuthorDto {
        validate(author.id != null) {
            "著者IDは必須です"
        }

        val updated = authorRepository.update(author.id, author)
            ?: throw EntityNotFoundException("著者", author.id)

        return AuthorDto.of(updated)
    }
}
