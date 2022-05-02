package com.kata.birthdaygreetings

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.dumbster.smtp.SimpleSmtpServer
import com.dumbster.smtp.SmtpMessage
import com.kata.birthdaygreetings.domain.*
import com.kata.birthdaygreetings.infrastructure.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class AcceptanceTest {
    lateinit var mailServer: SimpleSmtpServer

    @BeforeEach
    internal fun setUp() {
        mailServer = SimpleSmtpServer.start(9999)
    }

    @AfterEach
    internal fun tearDown() {
        mailServer.stop()
    }

    @Test
    internal fun oneEmployeeIsBornTodayAnotherIsNotBornToday() {
        val sendGreetings: () -> Either<MyError, Unit> =
            sendGreetingsWith(
                inMemoryLoadEmployees(),
                todayBirthdayEmployees,
                sendMailWith("localhost", 9999)
            )

        assertThat(sendGreetings()).isEqualTo(Right(Unit))

        val message = mailServer.receivedEmail.next() as SmtpMessage
        assertThat(message.body).isEqualTo("Happy birthday, dear TODAY_EMPLOYEE!")
    }

    @Test
    internal fun mailConnectionError() {
        val wrongPort: Long = 99

        val sendGreetings: () -> Either<MyError, Unit> =
            sendGreetingsWith(
                inMemoryLoadEmployees(),
                todayBirthdayEmployees,
                sendMailWith("localhost", wrongPort)
            )

        assertThat(sendGreetings()).isEqualTo(Left(MyError.SendMailError("Couldn't connect to host, port: localhost, 99; timeout -1")))
    }

    private fun inMemoryLoadEmployees(): () -> Either<Nothing, Employees> {
        val today = LocalDateTime.now()
        val tomorrow = today.plusDays(1)

        val inMemoryLoadEmployee: () -> Either<Nothing, Employees> = {
            Right(
                Employees(
                    listOf(
                        employeeBirthday(dateOfBirth(today), "TODAY_EMPLOYEE"),
                        employeeBirthday(dateOfBirth(tomorrow), "TOMORROW_EMPLOYEE")
                    )
                )
            )
        }
        return inMemoryLoadEmployee
    }

    private fun dateOfBirth(whenIsYourBirthday: LocalDateTime): DateOfBirth =
        DateOfBirth(whenIsYourBirthday.dayOfMonth, whenIsYourBirthday.monthValue, whenIsYourBirthday.year)

    private fun employeeBirthday(
        todayDateOfBirth: DateOfBirth,
        fistName: String
    ) = Employee(firstName = fistName, dateOfBirth = todayDateOfBirth)

}