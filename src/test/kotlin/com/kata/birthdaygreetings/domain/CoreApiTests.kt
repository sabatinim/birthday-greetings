package com.kata.birthdaygreetings.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CoreApiTests {

    private val TODAY = DateOfBirth(LocalDateTime.now().dayOfMonth, LocalDateTime.now().monthValue, LocalDateTime.now().year)
    private val TOMORROW = DateOfBirth(LocalDateTime.now().plusDays(1).dayOfMonth, LocalDateTime.now().plusDays(1).monthValue, LocalDateTime.now().plusDays(1).year)

    @Test
    internal fun filterEmployees() {
        val employees = employeesBirth(TODAY, TOMORROW)

        assertThat(todayBirthdayEmployees(employees))
            .isEqualTo(BirthdayEmployees(Employees(listOf(employeeWith(TODAY)))))
    }

    @Test
    internal fun noBirthdayEmployees() {
        val employees = employeesBirth(TOMORROW, TOMORROW)

        assertThat(todayBirthdayEmployees(employees))
            .isEqualTo(BirthdayEmployees(Employees(emptyList())))
    }

    private fun employeeWith(todayDateOfBirth: DateOfBirth) = Employee(dateOfBirth = todayDateOfBirth)

    private fun employeesBirth(vararg datesOfBirth: DateOfBirth): Employees =  Employees(datesOfBirth.map{ b-> employeeWith(b)})
}