package com.github.dhirabayashi.bookmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookManagerApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<BookManagerApplication>(*args)
}
