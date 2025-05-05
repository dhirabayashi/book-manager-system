package com.github.dhirabayashi.bookmanager.application.service

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
    fun listAll(): List<Author> {
        return authorRepository.findAll()
    }

    /**
     * 著者を登録する
     *
     * @param author 登録する著者
     * @return 登録した著者
     */
    @Transactional(rollbackFor = [Exception::class])
    fun save(author: Author): Author {
        return authorRepository.save(author)
    }
}
