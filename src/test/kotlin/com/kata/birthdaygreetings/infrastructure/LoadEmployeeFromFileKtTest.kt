package com.kata.birthdaygreetings.infrastructure

import arrow.core.Left
import arrow.core.Right
import com.kata.birthdaygreetings.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoadEmployeeFromFileKtTest {

    @Test
    internal fun loadEmployeeFromFile() {

        val loadEmployeeFromFile = loadEmployeeFrom("./target/test-classes/employees.txt")

        val employees = loadEmployeeFromFile()

        assertThat(employees).isEqualTo(Right(Employees(listOf(Employee("Marco","Sabatini", DateOfBirth(5,3,1983),
            EmailAddress("address@email.com")
        )))))
    }

    @Test
    internal fun fileNotFound() {
        val loadEmployeeFromFile = loadEmployeeFrom("NOT_EXIXSTING_FILE")

        val result = loadEmployeeFromFile()

        assertThat(result).isEqualTo(Left(MyError.LoadEmployeesError("File NOT_EXIXSTING_FILE doesn't exist")))
    }
}