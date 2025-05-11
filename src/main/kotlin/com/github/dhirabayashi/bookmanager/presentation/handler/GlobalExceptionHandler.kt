package com.github.dhirabayashi.bookmanager.presentation.handler

import com.github.dhirabayashi.bookmanager.application.exception.EntityNotFoundException
import com.github.dhirabayashi.bookmanager.domain.check.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleBookNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        logger.error("error", ex)

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to (ex.message ?: "entity not found")))
    }

    @ExceptionHandler(ValidationException::class)
    fun handleBadRequest(ex: ValidationException): ResponseEntity<Map<String, String>> {
        logger.error("error", ex)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to (ex.message ?: "invalid parameter")))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<Map<String, String>> {
        logger.error("error", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to (ex.message ?: "internal server error")))
    }
}