package com.kata.birthdaygreetings.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CoreApiTests {

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