package com.kata.birthdaygreetings.infrastructure

import arrow.core.Either.Left
import arrow.core.Either.Right
import com.kata.birthdaygreetings.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoadEmployeeFromFileTest {

    @Test
    internal fun loadEmployeeFromFile() {

        val loadEmployeeFromFile = loadEmployeeFrom("./target/test-classes/employees.txt")

        assertThat(loadEmployeeFromFile()).isEqualTo(Right(Employees(listOf(Employee("Marco",
            "Sabatini",
            DateOfBirth(5, 3, 1983),
            EmailAddress("address@email.com")
        )))))
    }

    @Test
    internal fun employeeNotValid() {

        val loadEmployeeFromFile = loadEmployeeFrom("./target/test-classes/employeesNotValid.txt")

        assertThat(loadEmployeeFromFile()).isEqualTo(Left(MyError.LoadEmployeesError("Error For input string: \"address@email.com\"")))
    }

    @Test
    internal fun fileNotFound() {

        val loadEmployeeFromFile = loadEmployeeFrom("NOT_EXIXSTING_FILE")

        assertThat(loadEmployeeFromFile()).isEqualTo(Left(MyError.LoadEmployeesError("File NOT_EXIXSTING_FILE doesn't exist")))
    }
}