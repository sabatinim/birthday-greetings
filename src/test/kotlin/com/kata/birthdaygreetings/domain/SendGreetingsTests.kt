package com.kata.birthdaygreetings.domain

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import com.kata.birthdaygreetings.domain.SendGreetingsTests.MyError.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

class SendGreetingsTests {

    sealed class MyError {
        data class LoadError(val msg: String) : MyError()
        data class SendMailError(val msg: String) : MyError()
    }

    fun sendGreetings(
        loadEmployees: () -> Either<MyError, Employees>,
        filterEmployees: (Employees) -> BirthdayEmployees,
        sendBirthdayMail: (BirthdayEmployees) -> Either<MyError, Unit>
    ): () -> Either<MyError, Unit> = { loadEmployees().flatMap { sendBirthdayMail(filterEmployees(it)) } }

    @Test
    fun sendGreetingsOk() {

        val loadEmployeesInMemory: () -> Either<MyError, Employees> = {
            Right(Employees(listOf(Employee())))
        }
        val fakeFilterEmployees: (Employees) -> BirthdayEmployees = {
            BirthdayEmployees(listOf(Employee()))
        }

        lateinit var actual: BirthdayEmployees
        val sendMailFake: (BirthdayEmployees) -> Either<MyError, Unit> = {
            actual = it
            Right(Unit)
        }

        val sendGreetings = sendGreetings(loadEmployeesInMemory, fakeFilterEmployees, sendMailFake)()

        assertThat(actual).isEqualTo(BirthdayEmployees(listOf(Employee())))
        assertThat(sendGreetings).isEqualTo(Right(Unit))

    }

    val UNUSED: (Employees) -> BirthdayEmployees = { BirthdayEmployees(emptyList()) }
    val ANOTHER_UNUSED: (BirthdayEmployees) -> Either<MyError, Unit> = { Right(Unit) }

    @Test
    internal fun loadEmployeeGoesInError() {

        val loadEmployeesGoesInError: () -> Either<MyError, Employees> = {
            Left(LoadError("EROR"))
        }

        val sendGreetings = sendGreetings(loadEmployeesGoesInError, UNUSED, ANOTHER_UNUSED)

        val result = sendGreetings()

        assertThat(result).isEqualTo(Left(LoadError("EROR")))

//        when (result) {
//            is Either.Left -> when (result.a) {
//                is LoadError -> assertThat(result.a).isEqualTo(Left(LoadError("EROR")))
//            }
//            is Either.Right -> fail("Shold Not reach here!!!")
//        }
    }

    @Test
    internal fun sendMailGoesInError() {

        val loadEmployeesInMemory: () -> Either<MyError, Employees> = {
            Right(Employees(listOf(Employee())))
        }
        val fakeFilterEmployees: (Employees) -> BirthdayEmployees = {
            BirthdayEmployees(listOf(Employee()))
        }
        val sendMailFake: (BirthdayEmployees) -> Either<MyError, Unit> = {
            Left(SendMailError("SEND_ERROR"))
        }

        val sendGreetings = sendGreetings(
            loadEmployeesInMemory, fakeFilterEmployees,
            sendMailFake
        )

        val result = sendGreetings()

        when (result) {
            is Either.Left -> when (result.a) {
                is SendMailError -> assertThat(result.a).isEqualTo(SendMailError("SEND_ERROR"))
            }
            is Either.Right -> fail("Shold Not reach here!!!")
        }
    }
}
