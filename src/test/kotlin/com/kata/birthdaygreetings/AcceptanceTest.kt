package com.kata.birthdaygreetings

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
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

    val TODAY = LocalDateTime.now()
    val TOMORROW = TODAY.plusDays(1)
    val WRONG_PORT: Long = 99

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
    internal fun oneEmployeeIsBornTodayAnotherIsNoBornToday() {

        val inMemoryLoadEmployee: () -> Either<Nothing, Employees> = {
            Right(
                Employees(
                    listOf(
                        employeeBirthday(dateOfBirth(TODAY), "TODAY_EMPLOYEE"),
                        employeeBirthday(dateOfBirth(TOMORROW), "TOMORROW_EMPLOYEE")
                    )
                )
            )
        }

        val sendGreetings: () -> Either<MyError, Unit> =
            sendGreetingsWith(
                inMemoryLoadEmployee,
                todayBirthdayEmployees,
                sendMailWith("localhost",9999)
            )

        val result = sendGreetings()

        val message = mailServer.receivedEmail.next() as SmtpMessage
        assertThat(message.body).isEqualTo("Happy birthday, dear TODAY_EMPLOYEE!")

        assertThat(result).isEqualTo(Right(Unit))

    }

    @Test
    internal fun mailConnectionError() {

        val inMemoryLoadEmployee: () -> Either<Nothing, Employees> = {
            Right(
                Employees(
                    listOf(
                        employeeBirthday(dateOfBirth(TODAY), "TODAY_EMPLOYEE")
                    )
                )
            )
        }

        val sendGreetings: () -> Either<MyError, Unit> =
            sendGreetingsWith(
                inMemoryLoadEmployee,
                todayBirthdayEmployees,
                sendMailWith("localhost", WRONG_PORT)
            )

        val result = sendGreetings()

        assertThat(result).isEqualTo(Left(MyError.SendMailError("Couldn't connect to host, port: localhost, 99; timeout -1")))
    }

    private fun dateOfBirth(whenIsYourBirthday: LocalDateTime): DateOfBirth =
        DateOfBirth(whenIsYourBirthday.dayOfMonth, whenIsYourBirthday.monthValue, whenIsYourBirthday.year)

    private fun employeeBirthday(
        todayDateOfBirth: DateOfBirth,
        fistName: String
    ) = Employee(firstName = fistName, dateOfBirth = todayDateOfBirth)

}