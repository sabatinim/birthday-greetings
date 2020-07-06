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
data class BirthDay(
    val day: Int,
    val month: Int

)


data class EmailAddress(val value: String)

data class Employees(val employeeGroup: List<Employee>)
data class BirthdayEmployees(val employeeGroup: List<Employee>)

data class GreetingsEmail(val subject: String, val body: String, val emailAddress: String)

private val today: () -> BirthDay = {
    val now = LocalDateTime.now()
    BirthDay(now.dayOfMonth, now.monthValue)
}

private fun isBirthday(whenIsYourBirthday: () -> BirthDay): (DateOfBirth) -> Boolean =
    {
        (whenIsYourBirthday().day == it.day) && whenIsYourBirthday().month == it.month
    }

private val todayIsYourBirthday: (DateOfBirth) -> Boolean = isBirthday(today)

private fun birthdayEmployees(isBirthDay: (DateOfBirth) -> Boolean): (Employees) -> BirthdayEmployees = {
    BirthdayEmployees(it.employeeGroup.filter { employee -> isBirthDay(employee.dateOfBirth) })
}

//PUBLIC FUNCTIONS
val todayBirthdayEmployees: (Employees) -> BirthdayEmployees = birthdayEmployees(todayIsYourBirthday)