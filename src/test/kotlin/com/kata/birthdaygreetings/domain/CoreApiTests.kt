package com.kata.birthdaygreetings.domain

import arrow.core.Either
import arrow.core.flatMap
import com.kata.birthdaygreetings.domain.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CoreApiTests {

//    // Either Style
//    fun parse(s: String): Either<NumberFormatException, Int> =
//        if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
//        else Either.Left(NumberFormatException("$s is not a valid integer."))
//
//    fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
//        if (i == 0) Either.Left(IllegalArgumentException("Cannot take reciprocal of 0."))
//        else Either.Right(1.0 / i)
//
//    fun stringify(d: Double): String = d.toString()
//
//    fun magic(s: String): Either<Exception, String> =
//        parse(s).flatMap { reciprocal(it) }.map { stringify(it) }
//
    @Test
    internal fun filterEmployees() {
        val now = LocalDateTime.now()
        val todayDateOfBirth = DateOfBirth(now.dayOfMonth, now.monthValue, now.year)

        val tomorrow = LocalDateTime.now().plusDays(1)
        val tomorrowDateOfBirth = DateOfBirth(tomorrow.dayOfMonth, tomorrow.monthValue, tomorrow.year)

        val employees = Employees(
            listOf(
                employeeBirthday(todayDateOfBirth),
                employeeBirthday(tomorrowDateOfBirth)
            )
        )

        val birthdayEmployees = todayBirthdayEmployees(employees)
        assertThat(birthdayEmployees)
            .isEqualTo(BirthdayEmployees(listOf(employeeBirthday(todayDateOfBirth))))
    }

    @Test
    internal fun noBirthdayEmployees() {

        val tomorrow = LocalDateTime.now().plusDays(1)
        val tomorrowDateOfBirth = DateOfBirth(tomorrow.dayOfMonth, tomorrow.monthValue, tomorrow.year)

        val employees = Employees(
            listOf(
                employeeBirthday(tomorrowDateOfBirth),
                employeeBirthday(tomorrowDateOfBirth)
            )
        )

        val birthdayEmployees = todayBirthdayEmployees(employees)

        assertThat(birthdayEmployees)
            .isEqualTo(BirthdayEmployees(emptyList()))
    }

    private fun employeeBirthday(todayDateOfBirth: DateOfBirth) = Employee(dateOfBirth = todayDateOfBirth)
}