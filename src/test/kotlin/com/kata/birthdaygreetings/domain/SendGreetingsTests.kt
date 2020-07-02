package com.kata.birthdaygreetings.domain

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.kata.birthdaygreetings.domain.MyError.LoadEmployeesError
import com.kata.birthdaygreetings.domain.MyError.SendMailError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test

class SendGreetingsTests {

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
            Left(LoadEmployeesError("EROR"))
        }

        val sendGreetings = sendGreetings(loadEmployeesGoesInError, UNUSED, ANOTHER_UNUSED)

        val result = sendGreetings()

        assertThat(result).isEqualTo(Left(LoadEmployeesError("EROR")))
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
