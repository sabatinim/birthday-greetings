package com.kata.birthdaygreetings.domain

import java.time.LocalDateTime

data class Employee(
    val firstName: String = "NAME", val lastName: String = "LASTNAME",
    val dateOfBirth: DateOfBirth = DateOfBirth(1, 1, 1),
    val emailAddress: EmailAddress = EmailAddress("EMAIL")
)

data class DateOfBirth(val day: Int, val month: Int, val year: Int)
data class BirthDay(val day: Int, val month: Int)
data class EmailAddress(val value: String)
data class Employees(val employeeGroup: List<Employee>)
data class BirthdayEmployees(val e: Employees)
data class GreetingsMessage(val subject: String, val body: String, val recipient: String)

private val today: () -> BirthDay = {
    val now = LocalDateTime.now()
    BirthDay(now.dayOfMonth, now.monthValue)
}

private fun isTodayYourBirthday(birth: DateOfBirth): Boolean =
    (today().day == birth.day) && today().month == birth.month

//PUBLIC API
fun todayBirthdayEmployees(employees: Employees): BirthdayEmployees =
    BirthdayEmployees(
        Employees(employees.employeeGroup.filter { employee -> isTodayYourBirthday(employee.dateOfBirth) })
    )