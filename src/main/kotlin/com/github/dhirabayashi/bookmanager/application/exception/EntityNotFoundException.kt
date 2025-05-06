package com.github.dhirabayashi.bookmanager.application.exception

class EntityNotFoundException(
    entityType: String,
    id: String,
) : RuntimeException(
    "${entityType}が見つかりません。id: $id"
)
