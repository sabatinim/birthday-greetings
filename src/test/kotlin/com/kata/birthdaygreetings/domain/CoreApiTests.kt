package com.kata.birthdaygreetings.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CoreApiTests {

    val TODAY = DateOfBirth(LocalDateTime.now().dayOfMonth, LocalDateTime.now().monthValue, LocalDateTime.now().year)
    val TOMORROW = DateOfBirth(LocalDateTime.now().plusDays(1).dayOfMonth, LocalDateTime.now().plusDays(1).monthValue, LocalDateTime.now().plusDays(1).year)

    @Test
    internal fun filterEmployees() {

        val employees = Employees(
            listOf(
                employeeBirthday(TODAY),
                employeeBirthday(TOMORROW)
            )
        )

        val birthdayEmployees = todayBirthdayEmployees(employees)
        assertThat(birthdayEmployees)
            .isEqualTo(BirthdayEmployees(listOf(employeeBirthday(TODAY))))
    }

    @Test
    internal fun noBirthdayEmployees() {

        val employees = Employees(
            listOf(
                employeeBirthday(TOMORROW),
                employeeBirthday(TOMORROW)
            )
        )

        val birthdayEmployees = todayBirthdayEmployees(employees)

        assertThat(birthdayEmployees)
            .isEqualTo(BirthdayEmployees(emptyList()))
    }

    private fun employeeBirthday(todayDateOfBirth: DateOfBirth) = Employee(dateOfBirth = todayDateOfBirth)
}