package com.kata.birthdaygreetings.domain

import arrow.core.Either
import arrow.core.flatMap

sealed class MyError {
    data class LoadEmployeesError(val msg: String) : MyError()
    data class SendMailError(val msg: String) : MyError()
}

typealias LoadEmployees = () -> Either<MyError, Employees>
typealias SendGreetingMessage = (BirthdayEmployees) -> Either<MyError, Unit>
typealias SendGreetings = () -> Either<MyError, Unit>

fun sendGreetingsWith(f: LoadEmployees, g: SendGreetingMessage): SendGreetings = { f().map(::todayBirthdayEmployees).flatMap(g) }
