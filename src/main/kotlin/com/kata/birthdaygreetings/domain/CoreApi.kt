package com.kata.birthdaygreetings.domain

import java.time.LocalDateTime

data class Employee(
    val firstName: String = "NAME",
    val lastName: String = "LASTNAME",
    val dateOfBirth: DateOfBirth = DateOfBirth(1, 1, 1),
    val emailAddress: EmailAddress = EmailAddress("EMAIL")
)

data class DateOfBirth(
    val day: Int,
    val month: Int,
    val year: Int
)

data class EmailAddress(val value: String)

data class Employees(val employeeGroup: List<Employee>)
data class BirthdayEmployees(val employeeGroup: List<Employee>)

data class GreetingsEmail(val subject: String, val text: String)
data class GreetingsEmails(val emailGroup: List<GreetingsEmail>)

private val today: () -> DateOfBirth = {
    val now = LocalDateTime.now()
    DateOfBirth(now.dayOfMonth, now.monthValue, now.year)
}

private fun isBirthday(whenIsYourBirthday: () -> DateOfBirth): (DateOfBirth) -> Boolean = { whenIsYourBirthday() == it }

private val todayIsYourBirthday: (DateOfBirth) -> Boolean = isBirthday(today)

private fun birthdayEmployees(isBirthDay: (DateOfBirth) -> Boolean): (Employees) -> BirthdayEmployees = {
    BirthdayEmployees(it.employeeGroup.filter { employee -> isBirthDay(employee.dateOfBirth) })
}

//PUBLIC FUNCTIONS
val todayBirthdayEmployees: (Employees) -> BirthdayEmployees = birthdayEmployees(todayIsYourBirthday)