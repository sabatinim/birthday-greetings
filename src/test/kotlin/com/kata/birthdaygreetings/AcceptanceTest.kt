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

    lateinit var mailServer: SimpleSmtpServer

    @BeforeEach
    internal fun setUp() {
        mailServer = SimpleSmtpServer.start(9999)
    }

    @AfterEach
    internal fun tearDown() {
        mailServer.stop()
    }

    val TODAY = LocalDateTime.now()
    val TOMORROW = TODAY.plusDays(1)

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

        val sendMailTo: (BirthdayEmployees) -> Either<MyError, Unit> =
        sendMailWith("localhost",9999)

        val sendGreetings =
            sendGreetings(
                inMemoryLoadEmployee,
                todayBirthdayEmployees,
                sendMailTo
            )

        val result = sendGreetings()

        val message = mailServer.receivedEmail.next() as SmtpMessage
        assertThat(message.body).isEqualTo("Happy birthday, dear TODAY_EMPLOYEE!")

        assertThat(result).isEqualTo(Right(Unit))

    }


    @Test
    internal fun mailConnectionError() {

        val sendMailTo: (BirthdayEmployees) -> Either<MyError, Unit> =
            sendMailWith("localhost", 99)

        val result = sendMailTo(
            BirthdayEmployees(
                listOf(
                    Employee()
                )
            )
        )

        assertThat(result).isEqualTo(Left(MyError.SendMailError("Couldn't connect to host, port: localhost, 99; timeout -1")))
    }

    private fun dateOfBirth(whenIsYourBirthday: LocalDateTime): DateOfBirth =
        DateOfBirth(whenIsYourBirthday.dayOfMonth, whenIsYourBirthday.monthValue, whenIsYourBirthday.year)

    private fun employeeBirthday(
        todayDateOfBirth: DateOfBirth,
        fistName: String
    ) = Employee(firstName = fistName, dateOfBirth = todayDateOfBirth)

}