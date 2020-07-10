package com.kata.birthdaygreetings.domain

import arrow.core.Either
import arrow.core.flatMap

sealed class MyError {
    data class LoadEmployeesError(val msg: String) : MyError()
    data class SendMailError(val msg: String) : MyError()
}

fun sendGreetingsWith(
    loadEmployees: () -> Either<MyError, Employees>,
    filterEmployees: (Employees) -> BirthdayEmployees,
    sendBirthdayNotificationTo: (BirthdayEmployees) -> Either<MyError, Unit>
): () -> Either<MyError, Unit> = {

    loadEmployees()
        .map {
            filterEmployees(it)
        }
        .flatMap(sendBirthdayNotificationTo)
            
}
